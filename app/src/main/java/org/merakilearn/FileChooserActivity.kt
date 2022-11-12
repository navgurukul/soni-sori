package org.merakilearn

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.ui.ScratchActivity
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class FileChooserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_chooser)

        val progressBar: ProgressBar = findViewById(R.id.progressBar3)
        progressBar.visibility = View.VISIBLE

        val intent = intent
        val action = intent.action
        val uri: Uri? = intent.data
        val file: File?
        val navigator: MerakiNavigator by inject()

        if (action!!.compareTo(Intent.ACTION_VIEW) == 0) {
            val scheme = intent.scheme
            if (scheme != null) {
                when {
                    scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0 -> {

                        file = fileFromContentUri(this, uri!!)

                        if (file.name.endsWith("py")) {
                            navigator.openPlaygroundWithFileContent(this, file)
                            finish()
                        }
                        else if (file.name.endsWith("sb3")) {
                            val newIntent = Intent(this, ScratchActivity::class.java)
                            newIntent.putExtra("file", file)
                            startActivity(newIntent)
                            finish()
                        }
                        else {
                            Toast.makeText(this,
                                "Currently, we only support python and scratch file",
                                Toast.LENGTH_SHORT).show()
                            finish()
                            onBackPressed()
                        }
                    }
                    scheme.compareTo(ContentResolver.SCHEME_FILE) == 0 -> {

                        file = File(uri!!.path!!)

                        if (file.name.endsWith("py")) {
                            navigator.openPlaygroundWithFileContent(this, file)
                            finish()
                        }
                        else if (file.name.endsWith("sb3")) {
                            val newIntent = Intent(this, ScratchActivity::class.java)
                            newIntent.putExtra("file", file)
                            startActivity(newIntent)
                            finish()
                        }
                        else {
                            Toast.makeText(this,
                                "Currently, we only support python and scratch file",
                                Toast.LENGTH_SHORT).show()
                            finish()
                            onBackPressed()
                        }
                    }
                    scheme.compareTo("http") == 0 -> {
                        // TODO Import from HTTP!
                    }
                    scheme.compareTo("ftp") == 0 -> {
                        // TODO Import from FTP!
                    }
                }
            }
        }
    }

    private fun fileFromContentUri(context: Context, contentUri: Uri): File {

        val fileExtension = getFileExtension(context, contentUri)
        val fileName = getNameFromContentUri(context, contentUri)
        val tempFileExtension = when (fileExtension) {
            "bin" -> {
                ".sb3"
            }
            "py" -> {
                ".py"
            }
            else -> {
                getExtensionFromName(fileName)
            }
        }

        val finalFileName = fileName.substringBeforeLast(".") + tempFileExtension
        val tempFile = File(context.getExternalFilesDir(null), finalFileName)

        when (tempFileExtension) {
            ".sb3" -> {
                try {
                    tempFile.createNewFile()
                    val inputStream = context.contentResolver.openInputStream(contentUri)
                    val dataLinkLoad: String =
                        Base64.encodeToString(inputStream!!.readBytes(), Base64.DEFAULT)
                    val fileOutputStream = FileOutputStream(tempFile)
                    val bos = BufferedOutputStream(fileOutputStream)
                    val bfile = Base64.decode(dataLinkLoad, Base64.DEFAULT)
                    bos.write(bfile)
                    bos.close()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            ".py" -> {
                try {
                    tempFile.createNewFile()
                    val inputStream = context.contentResolver.openInputStream(contentUri)
                    val fileOutputStream = FileOutputStream(tempFile)
                    val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                    val string = inputStream!!.reader().readText()
                    outputStreamWriter.write(string)
                    outputStreamWriter.close()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return tempFile
    }

    private fun getNameFromContentUri(context: Context, contentUri: Uri): String {
        val returnCursor: Cursor =
            context.contentResolver.query(contentUri, null, null, null, null)!!
        val nameColumnIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameColumnIndex)
        returnCursor.close()
        return name
    }

    private fun getFileExtension(context: Context, contentUri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(contentUri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    private fun getExtensionFromName(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length)
    }

}