package org.merakilearn.scratchjr

import android.Manifest
import org.merakilearn.scratchjr.R
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.Arrays
import java.util.Vector

/**
 * Main activity for Scratch Jr., consisting of a full-screen landscape WebView.
 *
 * This activity creates an embedded WebView, which runs the HTML5 app containing the majority of the source code.
 *
 * Special thanks to Benesse Corp. for providing access to their Android port, which helped inspire some of the source code here.
 *
 * @author markroth8
 */
class ScratchJrActivity

    : Activity() {
    /** Container containing the web view  */
    var container: RelativeLayout? = null
        private set

    /** Web browser containing the Scratch Jr. HTML5 webapp  */
    private var _webView: WebView? = null

    /** Maintains connection to database  */
    private var _databaseManager: DatabaseManager? = null

    /** Performs file IO  */
    private var _ioManager: IOManager? = null

    /** Manages sounds  */
    private var _soundManager: SoundManager? = null

    /** Manages recording of new sounds  */
    private var _soundRecorderManager: SoundRecorderManager? = null

    /**
     * Returns true when all resources are loaded and the app is initialized.
     */
    /** Set to true when the app is initialized. This is used for unit testing.  */
    var isAppInitialized: Boolean = false

    /** Set to true when the editor is initialized. This is used for unit testing.  */
    var isEditorInitialized: Boolean = false

    /** Set to true when the splash screen is done loading. This is used for unit testing.  */
    private var _splashDone = false

    /** Y starting and ending coordinate for soft keyboard scroll position  */
    private var _softKeyboardScrollPosY0 = 0
    private var _softKeyboardScrollPosY1 = 0

    /** Handler for posting delayed updates  */
    private val _handler = Handler()

    /** Run-time Permissions  */
    private val SCRATCHJR_CAMERA_MIC_PERMISSION = 1
    var cameraPermissionResult: Int = PackageManager.PERMISSION_DENIED
    var micPermissionResult: Int = PackageManager.PERMISSION_DENIED
    var readExtPermissionResult: Int = PackageManager.PERMISSION_DENIED

    /* Firebase analytics tracking */
    private var _FirebaseAnalytics: FirebaseAnalytics? = null

    /**
     * Project uri that need to be imported.
     */
    private val projectUris = ArrayList<Uri?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        _databaseManager = DatabaseManager(this)
        _ioManager = IOManager(this)
        _soundManager = SoundManager(this)
        _soundRecorderManager = SoundRecorderManager(this)
        setContentView(org.merakilearn.scratchjr.R.layout.activity_scratch_jr)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        container = findViewById<View>(R.id.container) as RelativeLayout
        _webView = findViewById<View>(org.merakilearn.scratchjr.R.id.webview) as WebView
        _webView!!.setBackgroundColor(0x00000000)
        _webView!!.clearCache(true)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.i(LOG_TAG, "Setting non-immersive full screen")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            setImmersiveMode()
        }
        configureWebView()
        registerSoftKeyboardPanner()
        /* URL to load once ready */
        var urlToLoad: String?
        if ((savedInstanceState != null) && savedInstanceState.containsKey(BUNDLE_KEY_URL)) {
            Log.i(LOG_TAG, "Restoring bundle state...")
            urlToLoad = savedInstanceState.getString(BUNDLE_KEY_URL)
            if (urlToLoad == null) {
                urlToLoad = INDEX_PAGE_URL
            }
        } else {
            urlToLoad = INDEX_PAGE_URL
        }
        _webView!!.loadUrl(urlToLoad)

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.setAcceptFileSchemeCookies(true)

        val it = intent
        if (it != null && it.data != null) {
            receiveProject(it.data)
        }

        _FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // When System UI bar is displayed, wait one second and then re-assert immersive mode.
        window.decorView.setOnSystemUiVisibilityChangeListener {
            _handler.postDelayed(
                { runOnUiThread { setImmersiveMode() } },
                1000
            )
        }
        requestPermissions()
    }

    /*
    Ask for all permissions when ScratchJr is first launched so that we are not asking a 5-7 year old to give permission
     */
    fun requestPermissions() {
        cameraPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        micPermissionResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        readExtPermissionResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (cameraPermissionResult == PackageManager.PERMISSION_GRANTED && micPermissionResult == PackageManager.PERMISSION_GRANTED && readExtPermissionResult == PackageManager.PERMISSION_GRANTED) {
            return
        }

        val tmp = Vector<String>(3)
        if (cameraPermissionResult != PackageManager.PERMISSION_GRANTED) {
            tmp.add(Manifest.permission.CAMERA)
        }
        if (micPermissionResult != PackageManager.PERMISSION_GRANTED) {
            tmp.add(Manifest.permission.RECORD_AUDIO)
        }
        if (readExtPermissionResult != PackageManager.PERMISSION_GRANTED) {
            tmp.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val tmpArray: Array<Any> = tmp.toTypedArray()
        val desiredPermissions = Arrays.copyOf(
            tmpArray, tmpArray.size,
            Array<String>::class.java
        )

        ActivityCompat.requestPermissions(
            this,
            desiredPermissions,
            SCRATCHJR_CAMERA_MIC_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == SCRATCHJR_CAMERA_MIC_PERMISSION) {
            var permissionId = 0
            for (permission in permissions) {
                if (permission == Manifest.permission.CAMERA) {
                    cameraPermissionResult = grantResults[permissionId]
                }
                if (permission == Manifest.permission.RECORD_AUDIO) {
                    micPermissionResult = grantResults[permissionId]
                }
                if (permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    readExtPermissionResult = grantResults[permissionId]
                }
                permissionId++
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            setImmersiveMode()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    // Check the WebView to see if we're on the editor page.
                    // If so, call the JavaScript to save the current project
                    // and return to the lobby.
                    val url = _webView!!.url
                    if (url != null) {
                        Log.i(LOG_TAG, url)
                        if (url.contains("home.html")) {
                            runJavaScript("Lobby.goHome()")
                        } else if (url.contains("gettingstarted.html")) {
                            runJavaScript("closeme()")
                        } else if (url.contains("index.html")) {
                            finish()
                        } else if (url.contains("editor.html")) {
                            runJavaScript("ScratchJr.goBack()")
                        } else if (_webView!!.canGoBack()) {
                            _webView!!.goBack()
                        }
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        _databaseManager?.open()
        _soundManager?.open()
        _soundRecorderManager?.open()
        runOnUiThread {
            _webView!!.onResume()
            CookieSyncManager.getInstance().startSync()
        }
        runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.onResume();")
    }

    override fun onPause() {
        super.onPause()
        runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.onPause();")
        runOnUiThread {
            _webView!!.onPause()
            CookieSyncManager.getInstance().stopSync()
        }
        _databaseManager?.close()
        _soundManager?.close()
        _soundRecorderManager?.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_KEY_URL, _webView!!.url)
    }

    override fun onNewIntent(it: Intent) {
        super.onNewIntent(it)
        if (it?.data != null) {
            receiveProject(it.data)
        }
    }

    private fun receiveProject(projectUri: Uri?) {
        if (!isSplashDone) {
            projectUris.add(projectUri)
            return
        }
        importProject(projectUri)
    }

    private fun importProject(projectUri: Uri?) {
        val PROJECT_EXTENSION = applicationContext.getString(R.string.share_extension_filter)
        val scheme = projectUri!!.scheme
        Log.i(LOG_TAG, "receiveProject(scheme): $scheme")
        Log.i(LOG_TAG, "receiveProject(path): " + projectUri.path)

        // if scheme isn't file or content, skip import
        if (scheme == null || !(scheme == ContentResolver.SCHEME_FILE || scheme == ContentResolver.SCHEME_CONTENT)) {
            return
        }
        // if scheme is file, then skip if filename doesn't have scratchjr project extension
        if (scheme == ContentResolver.SCHEME_FILE && !projectUri.path!!.matches(PROJECT_EXTENSION.toRegex())) {
            return
        }
        runOnUiThread {
            try {
                _ioManager?.receiveProject(this@ScratchJrActivity, projectUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val databaseManager: DatabaseManager?
        get() = _databaseManager

    val iOManager: IOManager?
        get() = _ioManager

    val soundManager: SoundManager?
        get() = _soundManager

    val soundRecorderManager: SoundRecorderManager?
        get() = _soundRecorderManager

    private fun setImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i(LOG_TAG, "Setting immersive mode")
            var immersiveStickyFlag = 0
            try {
                immersiveStickyFlag =
                    View::class.java.getField("SYSTEM_UI_FLAG_IMMERSIVE_STICKY").getInt(null)
            } catch (e: IllegalAccessException) {
                Log.e(LOG_TAG, "Reflection fail", e)
            } catch (e: IllegalArgumentException) {
                Log.e(LOG_TAG, "Reflection fail", e)
            } catch (e: NoSuchFieldException) {
                Log.e(LOG_TAG, "Reflection fail", e)
            }
            _webView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or immersiveStickyFlag)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        val webSettings = _webView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = false
        webSettings.displayZoomControls = false
        webSettings.loadWithOverviewMode = false
        webSettings.useWideViewPort = false
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // Enable cookie persistence
        CookieManager.setAcceptFileSchemeCookies(true)
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(_webView, true)
        } else {
            cookieManager.setAcceptCookie(true)
        }
        CookieSyncManager.createInstance(this)

        /* Object exposed to the JavaScript that makes it easy to bridge JavaScript and Java */
        val javaScriptDirectInterface: JavaScriptDirectInterface = JavaScriptDirectInterface(this)
        _webView!!.addJavascriptInterface(javaScriptDirectInterface, "AndroidInterface")
        _webView!!.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.e(LOG_TAG, description)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // Filter out Internet links and open those with the Android browser
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    return true
                }
                return false // Allow WebView to load url
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Sync cookies
                CookieSyncManager.getInstance().sync()

                // Track page load
                val parts = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val page =
                    parts[parts.size - 1].split("\\?".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0]
                _FirebaseAnalytics?.setCurrentScreen(view.context as Activity, page, null)
            }
        }
        _webView!!.requestFocus(View.FOCUS_DOWN)
        webSettings.allowFileAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.allowContentAccess = true

        // Configure the web chrome client to consume console.log
        // calls from JavaScript.
        Log.i(LOG_TAG, "Configurating webChromeClient")
        _webView!!.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.e(
                    LOG_TAG,
                    "JavaScript log, " + cm.sourceId() + ":" + cm.lineNumber() + ", " + cm.message()
                )
                return true
            }
        }
    }

    fun createNewProject() {
        runJavaScript("Home.createNewProject()")
    }

    var isSplashDone: Boolean
        get() = _splashDone
        set(done) {
            _splashDone = done
            while (projectUris.size > 0) {
                val uri = projectUris.removeAt(0)
                importProject(uri)
            }
        }

    /**
     * Click the "Home" button on the title screen (for unit testing).
     */
    fun goHome() {
        runJavaScript("gohome()")
    }

    /**
     * Run the given JavaScript in the web view.
     */
    fun runJavaScript(js: String) {
        runOnUiThread { _webView!!.loadUrl("javascript:$js") }
    }

    /**
     * log a Firebase analytics event for the app
     * @param category
     * @param action
     * @param label
     */
    fun logAnalyticsEvent(category: String?, action: String?, label: String?) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, label)
        if (action != null) {
            _FirebaseAnalytics?.logEvent(action, params)
        }
    }

    /**
     * Record the preferred place for the user: home, school, other, noanswer
     * @param place
     */
    fun setAnalyticsPlacePref(place: String?) {
        _FirebaseAnalytics?.setUserProperty("place_preference", place)
    }

    /**
     * Record a user property
     * @param key like "school"
     * @param value like "Central High"
     */
    fun setAnalyticsPref(key: String?, value: String?) {
        if (key != null) {
            _FirebaseAnalytics?.setUserProperty(key, value)
        }
    }

    fun translateAndScaleRectToContainerCoords(rect: RectF, devicePixelRatio: Float) {
        val wx = _webView!!.x
        val wy = _webView!!.y
        rect[wx + rect.left * devicePixelRatio, wy + rect.top * devicePixelRatio, wx + rect.right * devicePixelRatio] =
            wy + rect.bottom * devicePixelRatio
    }

    fun setSoftKeyboardScrollLocation(topYPx: Int, bottomYPx: Int) {
        _softKeyboardScrollPosY0 = topYPx
        _softKeyboardScrollPosY1 = bottomYPx
    }

    private val statusBarHeight: Int
        get() {
            val rectangle = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rectangle)
            return rectangle.top
        }

    /**
     * Android does not properly pan to the text fields in full screen mode, so
     * here we introduce some custom logic to pan when the soft keyboard appears.
     *
     * The technique used here was inspired by http://stackoverflow.com/questions/7417123/
     * android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible
     */
    private fun registerSoftKeyboardPanner() {
        container?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                private var _priorVisibleHeight = 0
                private var _currentAnimator: ObjectAnimator? = null

                override fun onGlobalLayout() {
                    val r = Rect()
                    container?.getWindowVisibleDisplayFrame(r)
                    val currentVisibleHeight = r.bottom - r.top

                    // Determine if visible height changed
                    if (currentVisibleHeight != _priorVisibleHeight) {
                        val screenHeight = container?.rootView?.height ?: 0
                        val coveredHeight = screenHeight - currentVisibleHeight

                        if (currentVisibleHeight < _priorVisibleHeight && coveredHeight > screenHeight / 4) {
                            // Keyboard probably just became visible

                            val elTop = _softKeyboardScrollPosY0
                            val elBottom = _softKeyboardScrollPosY1

                            // Determine the amount of the focus element covered by the keyboard
                            val elPixelsCovered = elBottom - currentVisibleHeight

                            // If any amount is covered
                            if (elPixelsCovered > 0) {
                                var panUpPixels = elPixelsCovered

                                // Prevent panning so much the top of the element becomes hidden
                                panUpPixels = if (panUpPixels > elTop) elTop else panUpPixels

                                // Prevent panning more than the keyboard height (which produces an empty gap in the screen)
                                panUpPixels = if (panUpPixels > (coveredHeight - statusBarHeight)) {
                                    coveredHeight - statusBarHeight
                                } else panUpPixels

                                // Pan up
                                cancelAnimator()
                                _currentAnimator = ObjectAnimator.ofFloat(
                                    container, "y", container?.y ?: 0f, -panUpPixels.toFloat()
                                ).apply {
                                    duration = SOFT_KEYBOARD_PAN_MS.toLong()
                                    start()
                                }
                            } else {
                                cancelAnimator()
                                _currentAnimator = ObjectAnimator.ofFloat(
                                    container, "y", container?.y ?: 0f, statusBarHeight.toFloat()
                                ).apply {
                                    duration = SOFT_KEYBOARD_PAN_MS.toLong()
                                    start()
                                }
                            }
                        } else if (currentVisibleHeight > _priorVisibleHeight) {
                            // Keyboard probably just became hidden

                            // Reset pan
                            cancelAnimator()
                            _currentAnimator = ObjectAnimator.ofFloat(
                                container, "y", container?.y ?: 0f, 0f
                            ).apply {
                                duration = SOFT_KEYBOARD_PAN_MS.toLong()
                                start()
                            }
                            setImmersiveMode()
                            runJavaScript("if (typeof(ScratchJr) !== 'undefined') ScratchJr.editDone();")
                        }

                        // Save usable height for the next comparison
                        _priorVisibleHeight = currentVisibleHeight
                    }
                }

                private fun cancelAnimator() {
                    _currentAnimator?.let { animator ->
                        if (animator.isStarted) {
                            animator.cancel()
                        }
                        _currentAnimator = null
                    }
                }
            })
    }

    companion object {
        /** Milliseconds to pan when showing the soft keyboard  */
        private const val SOFT_KEYBOARD_PAN_MS = 250

        /** Log tag for Scratch Jr. app  */
        private const val LOG_TAG = "ScratchJr"

        /** Bundle key in which the current url is stored  */
        private const val BUNDLE_KEY_URL = "url"

        /** The url of the index page  */
        private const val INDEX_PAGE_URL = "file:///android_asset/HTML5/index.html"
    }
}