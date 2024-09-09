package org.merakilearn.scratchjr

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.widget.ImageView
import android.widget.RelativeLayout
import org.json.JSONException
import org.json.JSONObject
import org.merakilearn.scratchjr.ScratchJrUtil.copyFile
import org.merakilearn.scratchjr.ScratchJrUtil.extension
import org.merakilearn.scratchjr.ScratchJrUtil.jsonArrayToStringArray
import org.merakilearn.scratchjr.ScratchJrUtil.mimeType
import org.merakilearn.scratchjr.ScratchJrUtil.removeFile
import org.merakilearn.scratchjr.ScratchJrUtil.zipProject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


/**
 * The methods in this inner class are exposed directly to JavaScript in the HTML5 pages
 * as AndroidInterface.
 *
 * @author markroth8
 */
class JavaScriptDirectInterface
/**
 * @param scratchJrActivity
 */ internal constructor(
    /** Activity hosting the webview running the JavaScript  */
    private val _activity: ScratchJrActivity
) {
    /** Current camera view, if active  */
    private var _cameraView: CameraView? = null

    /** Current camera mask, if active  */
    private var _cameraMask: ImageView? = null

    @JavascriptInterface
    fun log(message: String?) {
        Log.i(LOG_TAG, message!!)
    }

    @JavascriptInterface
    fun notifySplashDone() {
        Log.i(LOG_TAG, "Splash screen done loading")
        _activity.isSplashDone = true
    }

    @JavascriptInterface
    fun notifyDoneLoading() {
        Log.i(LOG_TAG, "Application is done loading")
        _activity.isAppInitialized = true
    }

    @JavascriptInterface
    fun notifyEditorDoneLoading() {
        Log.i(LOG_TAG, "Editor is done loading")
        _activity.isEditorInitialized = true
    }

    //////////////////////////////////////////////////////////////////////
    // audio_*
    @JavascriptInterface
    fun audio_sndfx(file: String?) {
        val soundManager = _activity.soundManager
        soundManager!!.playSoundEffect(file!!)
    }

    @JavascriptInterface
    fun audio_sndfxwithvolume(file: String?, volume: Float) {
        val soundManager = _activity.soundManager
        soundManager!!.playSoundEffectWithVolume(file!!, volume)
    }

    @JavascriptInterface
    fun audio_play(file: String?, volume: Float): Int {
        val soundManager = _activity.soundManager
        return soundManager!!.playSound(file!!)
    }

    @JavascriptInterface
    fun audio_isplaying(soundId: Int): Boolean {
        val soundManager = _activity.soundManager
        return soundManager!!.isPlaying(soundId)
    }

    @JavascriptInterface
    fun audio_stop(soundId: Int) {
        val soundManager = _activity.soundManager
        soundManager!!.stopSound(soundId)
    }

    //////////////////////////////////////////////////////////////////////
    // database_*
    @JavascriptInterface
    fun database_query(json: String?): String {
        var result: String
        try {
            val obj = JSONObject(json)
            val stmt = obj.getString("stmt")
            val valuesJSONArray = obj.getJSONArray("values")
            val values = jsonArrayToStringArray(valuesJSONArray)
            val databaseManager: DatabaseManager? = _activity.databaseManager
            result = databaseManager.query(stmt, values).toString()
        } catch (e: JSONException) {
            result = "JSON error: " + e.message
        } catch (e: DatabaseException) {
            result = "SQL error: " + e.getMessage()
        }
        return result
    }

    @JavascriptInterface
    fun database_stmt(json: String?): String {
        var result: String
        try {
            val obj = JSONObject(json)
            val stmt = obj.getString("stmt")
            val valuesJSONArray = obj.getJSONArray("values")
            val values = jsonArrayToStringArray(valuesJSONArray)
            val databaseManager: DatabaseManager? = _activity.databaseManager
            result = databaseManager.stmt(stmt, values).toString()
        } catch (e: JSONException) {
            result = "JSON error: " + e.message
        } catch (e: DatabaseException) {
            result = "SQL error: " + e.getMessage()
        }
        return result
    }

    //////////////////////////////////////////////////////////////////////
    // io_*
    @JavascriptInterface
    fun io_getmd5(str: String?): String {
        val ioManager: IOManager? = _activity.iOManager
        return ioManager.md5(str)
    }

    @JavascriptInterface
    fun io_getsettings(): String {
        val homeDirectory = ""
        val choice = ""
        val soundPermission =
            if ((_activity.micPermissionResult == PackageManager.PERMISSION_GRANTED)) "YES" else "NO"
        val cameraPermission =
            if ((_activity.cameraPermissionResult == PackageManager.PERMISSION_GRANTED)) "YES" else "NO"
        return "$homeDirectory,$choice,$soundPermission,$cameraPermission"
    }

    @JavascriptInterface
    fun io_cleanassets(fileType: String?) {
        val ioManager: IOManager? = _activity.iOManager
        try {
            ioManager.cleanAssets(fileType)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Could not clean assets", e)
        }
    }

    @JavascriptInterface
    fun io_setfile(filename: String, base64ContentStr: String?): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.setFile(filename, base64ContentStr)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not set file '$filename'", e
            )
            result = "-1"
        }
        return result
    }

    @JavascriptInterface
    fun io_getfile(filename: String): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.getFile(filename)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not get file '$filename'", e
            )
            result = ""
        }
        return result
    }

    @JavascriptInterface
    fun io_setmedia(base64ContentStr: String?, extension: String): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.setMedia(base64ContentStr, extension)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not set media of type '$extension'", e
            )
            result = "-1"
        }
        return result
    }

    @JavascriptInterface
    fun io_setmedianame(contents: String?, key: String, ext: String): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.setMediaName(contents, key, ext)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not set media name of key '$key' ext '$ext'", e
            )
            result = "-1"
        }
        return result
    }

    @JavascriptInterface
    fun io_getmedia(filename: String): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.getMedia(filename)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not get media with filename '$filename'", e
            )
            result = "-1"
        }
        return result
    }

    @JavascriptInterface
    fun io_getmediadata(filename: String?, offset: Int, length: Int): String {
        val ioManager: IOManager? = _activity.iOManager
        return ioManager.getMediaData(filename, offset, length)
    }

    @JavascriptInterface
    fun io_getmedialen(file: String, key: String): Int {
        var result: Int
        val ioManager: IOManager? = _activity.iOManager
        try {
            result = ioManager.getMediaLen(file, key)
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not get media len for file '$file' key '$key'", e
            )
            result = 0
        }
        return result
    }

    @JavascriptInterface
    fun io_getmediadone(filename: String?): String {
        val ioManager: IOManager? = _activity.iOManager
        ioManager.getMediaDone(filename)
        return "1"
    }

    @JavascriptInterface
    fun io_remove(filename: String): String {
        var result: String
        val ioManager: IOManager? = _activity.iOManager

        Log.d(
            LOG_TAG,
            "Trying to remove filename '$filename'"
        )

        try {
            result = if (ioManager.remove(filename)) "1" else "-1"
        } catch (e: IOException) {
            Log.e(
                LOG_TAG,
                "Could not remove file '$filename'", e
            )
            result = "-1"
        }
        return result
    }

    //////////////////////////////////////////////////////////////////////
    // recordsound_*
    @JavascriptInterface
    fun recordsound_recordstart(): String {
        val soundRecorderManager = _activity.soundRecorderManager
        val soundFile = soundRecorderManager!!.startRecord()
        return if ((soundFile == null)) "-1" else soundFile
    }

    @JavascriptInterface
    fun recordsound_recordstop(): String {
        val soundRecorderManager = _activity.soundRecorderManager
        try {
            return if (soundRecorderManager!!.stopRecord()) "1" else "-1"
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "Error stopping recording", t)
            return "-1"
        }
    }

    @JavascriptInterface
    fun recordsound_volume(): Double {
        val soundRecorderManager = _activity.soundRecorderManager
        return soundRecorderManager!!.volume
    }

    @JavascriptInterface
    fun recordsound_startplay(): String {
        val soundRecorderManager = _activity.soundRecorderManager
        try {
            return soundRecorderManager!!.startPlay().toString()
        } catch (e: IllegalStateException) {
            Log.e(LOG_TAG, "Error starting play", e)
            return "ERROR: " + e.message
        }
    }

    @JavascriptInterface
    fun recordsound_stopplay(): String {
        val soundRecorderManager = _activity.soundRecorderManager
        try {
            soundRecorderManager!!.stopPlay()
            return "1"
        } catch (e: IllegalStateException) {
            Log.e(LOG_TAG, "Error stopping play", e)
            return "-1"
        }
    }

    @JavascriptInterface
    fun recordsound_recordclose(keep: String): String {
        val keepBoolean = keep.lowercase() != "no"
        val soundRecorderManager = _activity.soundRecorderManager
        soundRecorderManager!!.recordClose(keepBoolean)
        return if (keepBoolean) "1" else "-1"
    }

    //////////////////////////////////////////////////////////////////////
    // scratchjr_*
    @JavascriptInterface
    fun scratchjr_cameracheck(): String {
        return if (_activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) "1" else "0"
    }

    @JavascriptInterface
    fun scratchjr_has_multiple_cameras(): Boolean {
        return Camera.getNumberOfCameras() > 1
    }

    @JavascriptInterface
    fun scratchjr_startfeed(str: String): String {
        try {
            val obj = JSONObject(str)
            val imageDataStr = obj.getString("image")
            if (!imageDataStr.startsWith("data:image/png")) {
                Log.e(
                    LOG_TAG,
                    "Expecting data URL for image data but got '$imageDataStr'"
                )
                return "-1"
            }
            val base64ImageData = imageDataStr.substring(imageDataStr.indexOf(";base64,") + 8)
            val imageContent = Base64.decode(base64ImageData, Base64.NO_WRAP)

            val x = obj.getDouble("x").toFloat()
            val y = obj.getDouble("y").toFloat()
            val width = obj.getDouble("width").toFloat()
            val height = obj.getDouble("height").toFloat()
            val r = RectF(x, y, x + width, y + height)

            val mx = obj.getDouble("mx").toFloat()
            val my = obj.getDouble("my").toFloat()
            val mw = obj.getDouble("mw").toFloat()
            val mh = obj.getDouble("mh").toFloat()
            val r2 = RectF(mx, my, mx + mw, my + mh)

            val scale = obj.getDouble("scale").toFloat()
            val devicePixelRatio = obj.getDouble("devicePixelRatio").toFloat()
            openFeed(r, scale, devicePixelRatio, imageContent, r2)
        } catch (e: JSONException) {
            Log.e(
                LOG_TAG,
                "Could not decode json: '$str'", e
            )
            return "-1"
        }
        return "1"
    }

    @JavascriptInterface
    fun scratchjr_stopfeed(): String {
        closeFeed()
        return "1"
    }

    @JavascriptInterface
    fun scratchjr_choosecamera(facing: String): String {
        var result = "-1"
        if (_cameraView != null) {
            result = if (_cameraView.setCameraFacing(facing == "front")) {
                "1"
            } else {
                "-1"
            }
        }
        return result
    }

    @JavascriptInterface
    fun scratchjr_captureimage(onCameraCaptureComplete: String) {
        _cameraView.captureStillImage(
            PictureCallback { jpegData, camera ->
                sendBase64Image(
                    onCameraCaptureComplete,
                    jpegData
                )
            },
            Runnable {
                Log.e(LOG_TAG, "Could not capture picture")
                reportImageError(onCameraCaptureComplete)
            }
        )
    }

    @JavascriptInterface
    fun scratchjr_getgettingstartedvideopath(): String {
        val cacheDir = _activity.cacheDir
        val videoFile = File(cacheDir, "intro.mp4")
        if (!videoFile.exists()) {
            copyVideoToCacheDir()
        }
        if (!videoFile.exists()) {
            Log.w(LOG_TAG, "Video file does not exist after copying: '" + videoFile.path + "'")
        }
        return videoFile.path
    }

    @JavascriptInterface
    fun scratchjr_stopserver(): String {
        // On Android, we don't use an HTTP server - everything gets invoked directly.
        return "1"
    }

    @JavascriptInterface
    fun scratchjr_setsoftkeyboardscrolllocation(topYPx: Int, bottomYPx: Int) {
        _activity.setSoftKeyboardScrollLocation(topYPx, bottomYPx)
    }

    /**
     * Pop up the soft keyboard if this is not a hardware-keyboard device.
     */
    @JavascriptInterface
    fun scratchjr_forceShowKeyboard() {
        val mgr = _activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(_activity.currentFocus, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Hide the soft keyboard if this is not a hardware-keyboard device.
     */
    @JavascriptInterface
    fun scratchjr_forceHideKeyboard() {
        val mgr = _activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(_activity.currentFocus!!.windowToken, 0)
    }

    private fun sendBase64Image(onCameraCaptureComplete: String, jpegData: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        Log.i(LOG_TAG, "Picture size: " + bitmap.width + " x " + bitmap.height)
        val exifRotation: Int = CameraExif.getOrientation(jpegData)
        Log.i(
            LOG_TAG,
            "Picture rotation: $exifRotation"
        )
        val translatedJpegData: ByteArray = _cameraView.getTransformedImage(bitmap, exifRotation)
        val base64Data = Base64.encodeToString(translatedJpegData, Base64.NO_WRAP)
        closeFeed()
        _activity.runJavaScript("$onCameraCaptureComplete('$base64Data');")
    }

    private fun reportImageError(onCameraCaptureComplete: String) {
        _activity.runJavaScript("$onCameraCaptureComplete('error getting a still');")
    }

    private fun openFeed(
        rect: RectF, scale: Float, devicePixelRatio: Float, maskImageData: ByteArray,
        maskRect: RectF
    ) {
        _activity.runOnUiThread {
            _activity.translateAndScaleRectToContainerCoords(rect, devicePixelRatio)
            _activity.translateAndScaleRectToContainerCoords(maskRect, devicePixelRatio)
            scaleRectFromCenter(rect, scale)
            scaleRectFromCenter(maskRect, scale)
            val container = _activity.container

            _cameraView = CameraView(
                _activity,
                rect,
                scale * devicePixelRatio,
                true
            ) // always start with front-facing camera
            container!!.addView(
                _cameraView,
                RelativeLayout.LayoutParams(
                    rect.width().toInt(),
                    rect.height().toInt()
                )
            )
            _cameraView.setX(rect.left)
            _cameraView.setY(rect.top)

            _cameraMask = ImageView(_activity)
            val bitmap = BitmapFactory.decodeByteArray(maskImageData, 0, maskImageData.size)
            _cameraMask!!.setImageBitmap(bitmap)
            container.addView(
                _cameraMask,
                RelativeLayout.LayoutParams(
                    maskRect.width().toInt(),
                    maskRect.height().toInt()
                )
            )
            _cameraMask!!.x = maskRect.left
            _cameraMask!!.y = maskRect.top
        }
    }

    private fun scaleRectFromCenter(rect: RectF, scale: Float) {
        val deltaWidth = rect.width() * scale - rect.width()
        val deltaHeight = rect.height() * scale - rect.height()
        rect.left -= deltaWidth / 2
        rect.top -= deltaHeight / 2
        rect.right += deltaWidth / 2
        rect.bottom += deltaHeight / 2
    }

    private fun closeFeed() {
        _activity.runOnUiThread {
            val container = _activity.container
            if (_cameraView != null) {
                container!!.removeView(_cameraView)
                _cameraView = null
            }
            if (_cameraMask != null) {
                container!!.removeView(_cameraMask)
                _cameraMask = null
            }
        }
    }

    private fun copyVideoToCacheDir() {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            val cacheDir = _activity.cacheDir
            val videoFile = File(cacheDir, "intro.mp4")
            val buffer = ByteArray(1024)
            var len: Int
            `in` = _activity.assets.open("HTML5/assets/lobby/intro.mp4")
            out = FileOutputStream(videoFile)
            while ((`in`.read(buffer).also { len = it }) != -1) {
                out.write(buffer, 0, len)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Could not copy video to cache dir", e)
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Could not close input stream while copying video file", e)
                }
            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "Could not close output stream while copying video file", e)
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Sharing
    @JavascriptInterface
    fun deviceName(): String {
        return Build.MODEL
    }

    @JavascriptInterface
    fun createZipForProject(projectData: String, metadataJson: String?, name: String): String {
        // clean up old zip files
        _activity.iOManager.cleanZips()
        // create a temp folder
        val tempFolder =
            File(_activity.cacheDir.toString() + File.separator + UUID.randomUUID().toString())
        val projectFolder = File(tempFolder.path + File.separator + "project")
        projectFolder.mkdirs()
        // save data.json
        // Log.d(LOG_TAG, "writing data.json");
        val dataFile = File(projectFolder.absolutePath + File.separator + "data.json")
        try {
            val outputStream = FileOutputStream(dataFile)
            outputStream.write(projectData.toByteArray())
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return "error"
        }
        // Log.d(LOG_TAG, "writing data.json done");
        // copy assets to target folder
        val metadata: JSONObject
        try {
            metadata = JSONObject(metadataJson)
        } catch (e: JSONException) {
            e.printStackTrace()
            return "error"
        }
        // Log.d(LOG_TAG, "copying assets");
        val keys = metadata.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val folder = File(projectFolder.absolutePath + File.separator + key)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val files = metadata.optJSONArray(key) ?: continue
            for (i in 0 until files.length()) {
                val file = files.optString(i) ?: continue
                val srcFile = File(_activity.filesDir.toString() + File.separator + file)
                if (!srcFile.exists()) {
                    Log.e(
                        LOG_TAG,
                        "src file not exists$file"
                    )
                    continue
                }
                val targetFile = File(folder.absolutePath + File.separator + file)
                // Log.d(LOG_TAG, "copying assets" + file);
                try {
                    copyFile(srcFile, targetFile)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        // create zip file
        val extension = extension
        val fullName = name + extension
        zipProject(
            projectFolder.absolutePath,
            _activity.cacheDir.toString() + File.separator + fullName
        )
        // remove the temp folder
        removeFile(tempFolder)
        return fullName
    }

    @JavascriptInterface
    fun sendSjrUsingShareDialog(
        fileName: String, emailSubject: String?,
        emailBody: String?, shareType: Int
    ) {
        // Write a temporary file with the project data passed in from JS
        val mimetype = mimeType
        val file = File(_activity.cacheDir.toString() + File.separator + fileName)
        Log.d(LOG_TAG, file.absolutePath)
        val it = Intent(Intent.ACTION_SEND)
        it.setType(mimetype)
        it.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>())
        it.putExtra(Intent.EXTRA_SUBJECT, fileName)
        it.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailBody))

        // The stream data is a reference to the temporary file provided by our contentprovider
        it.putExtra(
            Intent.EXTRA_STREAM,
            Uri.parse(
                "content://" + ShareContentProvider.AUTHORITY + "/"
                        + fileName
            )
        )
        val shareIntent = Intent.createChooser(it, null)

        _activity.startActivity(shareIntent)
    }

    // Analytics
    @JavascriptInterface
    fun analyticsEvent(category: String?, action: String?, label: String?) {
        _activity.logAnalyticsEvent(category, action, label)
    }

    @JavascriptInterface
    fun setAnalyticsPlacePref(place: String?) {
        if (place != null) {
            _activity.setAnalyticsPlacePref(place)
        }
    }

    /**
     * Record a user property
     * @param key like "school"
     * @param propertyString like "Central High"
     */
    @JavascriptInterface
    fun setAnalyticsPref(key: String?, propertyString: String?) {
        if (key != null) {
            _activity.setAnalyticsPref(key, propertyString)
        }
    }

    companion object {
        private const val LOG_TAG = "ScratchJr.JSDirect"
    }
}