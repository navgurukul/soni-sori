package org.navgurukul.webide.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.navgurukul.webIDE.R
import org.navgurukul.webIDE.databinding.ActivityWebBinding
import org.navgurukul.webIDE.databinding.DialogInputSingleBinding
import org.navgurukul.webIDE.databinding.SheetLogsBinding
import org.navgurukul.webIDE.databinding.SheetWebSettingsBinding
import org.navgurukul.webide.ui.adapter.LogsAdapter
import org.navgurukul.webide.util.Constants
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.net.HyperServer
import org.navgurukul.webide.util.net.NetworkUtils
import org.navgurukul.webide.util.project.ProjectManager
import timber.log.Timber
import java.io.IOException
import java.util.*

class WebActivity : ThemedActivity() {

    private var jsLogs = ArrayList<ConsoleMessage>()

    private lateinit var localUrl: String
    private lateinit var localWithoutIndex: String

    private lateinit var binding : ActivityWebBinding


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        val project = intent.getStringExtra("name")
        NetworkUtils.server = HyperServer(this,project!!)
        super.onCreate(savedInstanceState)

        try {
            NetworkUtils.server!!.start()
        } catch (e: IOException) {
            Timber.e(e)
        }
        binding = ActivityWebBinding.inflate(LayoutInflater.from(this@WebActivity))
        setContentView(binding.root)
        val indexFile = ProjectManager.getIndexFile(this,project)
        val indexPath = ProjectManager.getRelativePath(this,indexFile!!, project)

        binding.include.toolbar.title = project
        setSupportActionBar(binding.include.toolbar)
        binding.webView.settings.javaScriptEnabled = true
        localUrl = if (NetworkUtils.server!!.wasStarted() && NetworkUtils.server!!.isAlive && NetworkUtils.ipAddress != null)
            "http://${NetworkUtils.ipAddress}:${HyperServer.PORT_NUMBER}/$indexPath"
        else
            intent.getStringExtra("localUrl")!!

        localWithoutIndex = localUrl.substring(0, localUrl.length - 10)
        binding.webView.loadUrl(localUrl)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.loadingProgress.progress = newProgress
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                jsLogs.add(consoleMessage)
                return true
            }

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                AlertDialog.Builder(this@WebActivity)
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> result.confirm() }
                        .setCancelable(false)
                        .show()

                return true
            }

            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                AlertDialog.Builder(this@WebActivity)
                        .setTitle("Confirm")
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> result.confirm() }
                        .setNegativeButton("CANCEL") { _, _ -> result.cancel() }
                        .setCancelable(false)
                        .show()

                return true
            }
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, localUrl: String) {
                super.onPageFinished(view, localUrl)
                binding.webView.animate().alpha(1F)
            }
        }

        binding.include.toolbar.subtitle = localUrl
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.server!!.stop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_web, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = defaultPrefs(this)
        when (item.itemId) {
            R.id.refresh -> {
                binding.webView.animate().alpha(0F)
                binding.webView.reload()
                return true
            }
            R.id.user_agent -> {
                val selectedI = IntArray(1)
                val current = binding.webView.settings.userAgentString
                val agents = LinkedList(Arrays.asList(*Constants.USER_AGENTS))
                if (!agents.contains(current)) agents.add(0, current)
                val parsedAgents = NetworkUtils.parseUAList(agents)
                AlertDialog.Builder(this)
                        .setTitle("Change User Agent")
                        .setSingleChoiceItems(parsedAgents.toTypedArray(), parsedAgents.indexOf(NetworkUtils.parseUA(current))) { _, i -> selectedI[0] = i }
                        .setPositiveButton("UPDATE") { _, _ -> binding.webView.settings.userAgentString = agents[selectedI[0]] }
                        .setNeutralButton("RESET") { _, _ -> binding.webView.settings.userAgentString = null }
                        .setNegativeButton("CUSTOM") { _, _ ->
                            val rootView = DialogInputSingleBinding.inflate(LayoutInflater.from(this@WebActivity))
                            rootView.inputText.hint = "Custom agent string"
                            rootView.inputText.setText(current)
                            AlertDialog.Builder(this@WebActivity)
                                    .setTitle("Custom User Agent")
                                    .setView(rootView.root)
                                    .setPositiveButton("UPDATE") { _, _ -> binding.webView.settings.userAgentString = rootView.inputText.text.toString() }
                                    .setNegativeButton(R.string.cancel, null)
                                    .show()
                        }
                        .show()
                return true
            }
            R.id.web_browser -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(localUrl))
                startActivity(intent)
                return true
            }
            R.id.web_logs -> {
                val layoutLog = SheetLogsBinding.inflate(LayoutInflater.from(this@WebActivity))
                val darkTheme = prefs["dark_theme", false]!!
                if (darkTheme) {
                    layoutLog.root.setBackgroundColor(-0xcccccd)
                }

                val manager = LinearLayoutManager(this)
                val adapter = LogsAdapter(localWithoutIndex, jsLogs, darkTheme)

                layoutLog.logsList.layoutManager = manager
                layoutLog.logsList.addItemDecoration(DividerItemDecoration(this@WebActivity, manager.orientation))
                layoutLog.logsList.adapter = adapter

                val dialogLog = BottomSheetDialog(this)
                dialogLog.setContentView(layoutLog.root)
                dialogLog.show()
                return true
            }
            R.id.web_settings -> {
                val layout = SheetWebSettingsBinding.inflate(LayoutInflater.from(this@WebActivity))
                if (prefs["dark_theme", false]!!) {
                    layout.root.setBackgroundColor(-0xcccccd)
                }

                layout.allowContentAccess.isChecked = binding.webView.settings.allowContentAccess
                layout.allowFileAccess.isChecked = binding.webView.settings.allowFileAccess
                layout.blockNetworkImage.isChecked = binding.webView.settings.blockNetworkImage
                layout.blockNetworkLoads.isChecked = binding.webView.settings.blockNetworkLoads
                layout.builtInZoomControls.isChecked = binding.webView.settings.builtInZoomControls
                layout.database.isChecked = binding.webView.settings.databaseEnabled
                layout.displayZoomControls.isChecked = binding.webView.settings.displayZoomControls
                layout.domStorage.isChecked = binding.webView.settings.domStorageEnabled
                layout.jsCanOpenWindows.isChecked = binding.webView.settings.javaScriptCanOpenWindowsAutomatically
                layout.jsEnabled.isChecked = binding.webView.settings.javaScriptEnabled
                layout.loadOverview.isChecked = binding.webView.settings.loadWithOverviewMode
                layout.imageLoad.isChecked = binding.webView.settings.loadsImagesAutomatically
                layout.wideView.isChecked = binding.webView.settings.useWideViewPort

                layout.allowContentAccess.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.allowContentAccess = isChecked }
                layout.allowFileAccess.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.allowFileAccess = isChecked }
                layout.blockNetworkImage.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.blockNetworkImage = isChecked }
                layout.blockNetworkLoads.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.blockNetworkLoads = isChecked }
                layout.builtInZoomControls.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.builtInZoomControls = isChecked }
                layout.database.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.databaseEnabled = isChecked }
                layout.displayZoomControls.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.displayZoomControls = isChecked }
                layout.domStorage.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.domStorageEnabled = isChecked }
                layout.jsCanOpenWindows.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.javaScriptCanOpenWindowsAutomatically = isChecked }
                layout.jsEnabled.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.javaScriptEnabled = isChecked }
                layout.loadOverview.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.loadWithOverviewMode = isChecked }
                layout.imageLoad.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.loadsImagesAutomatically = isChecked }
                layout.wideView.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.useWideViewPort = isChecked }


                if (Build.VERSION.SDK_INT >= 16) {
                    layout.allowFileAccessFromFileUrls.isChecked = binding.webView.settings.allowFileAccessFromFileURLs
                    layout.allowUniversalAccessFromFileUrls.isChecked = binding.webView.settings.allowUniversalAccessFromFileURLs
                    layout.allowFileAccessFromFileUrls.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.allowFileAccessFromFileURLs = isChecked }
                    layout.allowUniversalAccessFromFileUrls.setOnCheckedChangeListener { _, isChecked -> binding.webView.settings.allowUniversalAccessFromFileURLs = isChecked }
                } else {
                    layout.allowFileAccessFromFileUrls.visibility = View.GONE
                    layout.allowUniversalAccessFromFileUrls.visibility = View.GONE
                }

                val dialog = BottomSheetDialog(this)
                dialog.setContentView(layout.root)
                dialog.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
