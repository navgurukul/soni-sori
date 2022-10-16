package org.merakilearn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import org.merakilearn.R
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.playground.editor.PythonEditorFragment
import java.io.*


@Parcelize
object ScratchActivityArgs: Parcelable
class ScratchActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar:ProgressBar
    var fileName:String="Avinash"
    lateinit var datalinksave:String



    companion object {
        fun start(context: Context) {
            val intent = Intent(context,ScratchActivity::class.java).apply {
                putExtras(ScratchActivityArgs.toBundle()!!)
            }
            context.startActivity(intent)
        }
    }
    private val args: ScratchActivityArgs? by lazy {
        intent.extras?.getParcelable(KEY_ARG)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch)

        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progressBar)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient= WebChromeClient()
        webView.loadUrl("file:///android_asset/index.html")
       //webView.loadUrl("javascript:loadProjectUsingBase64('"+fileName+","+datalinksave+"')")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled=true
        webView.settings.setSupportZoom(true)
        webView.addJavascriptInterface(this, "Scratch")



    }




@Throws(FileNotFoundException::class, IOException::class)
open fun saveBase64StringToFile(
    base64Str: String?,
    filePath: String?,
    fileName: String?
) {
    var bos: BufferedOutputStream? = null
    var fos: FileOutputStream? = null
    var file: File? = null
    try {
        val dir = File(filePath)
        if (!dir.exists() && dir.isDirectory) {
            dir.mkdirs()
        }
        file = File(filePath, fileName)
        fos = FileOutputStream(file)
        bos = BufferedOutputStream(fos)
        val bfile = Base64.decode(base64Str, Base64.DEFAULT)
        bos.write(bfile)
        println("File Saved")

    } catch (e: FileNotFoundException) {
        throw e
    } catch (e: IOException) {
        throw e
    } finally {
        if (bos != null) {
            try {
                bos.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
        if (fos != null) {
            try {
                fos.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }
}


    @JavascriptInterface
    fun onsave() {

    }

    @JavascriptInterface
    fun onBack() {
            finish()
            Toast.makeText(this,"Scratch Exit",Toast.LENGTH_LONG).show()
    }

    @JavascriptInterface
    fun getBase64StringFromWeb(myIds: String) {
        datalinksave=myIds
        println(datalinksave);
    }

    fun SaveScratchFile()
    {
        saveBase64StringToFile(datalinksave,this.filesDir.path,"Trust6.sb3")
    }


    // Overriding WebViewClient functions
    inner class WebChromeClient: android.webkit.WebChromeClient()
    {

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
              println("Data is passed")
//            val builder: AlertDialog.Builder = AlertDialog.Builder(this@ScratchActivity)
//            builder.setMessage(message).setPositiveButton(android.R.string.ok,
//                    DialogInterface.OnClickListener { dialog, which ->
//                       result!!.confirm()
//                        dialog.dismiss()
//                    })
//                .setNegativeButton(android.R.string.cancel,
//                DialogInterface.OnClickListener { dialog, which ->
//                     result!!.cancel()
//                    }).show()
                 return true
        }
    }
    inner class WebViewClient : android.webkit.WebViewClient() {
        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }


        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
           // webView.loadUrl("javascript:loadProjectUsingBase64('"+fileName+"','"+datalinksave+"')")
            //webView.loadUrl("javascript:Scratch.getBase64StringFromWeb(globalBase64String);");
            progressBar.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val context: Context = this
    }


}





