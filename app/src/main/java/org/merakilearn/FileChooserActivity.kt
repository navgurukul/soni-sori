package org.merakilearn

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.koin.android.ext.android.inject
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.ui.ScratchActivity
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class FileChooserActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_chooser)
        checkAndRequestPermissions()

        val progressBar: ProgressBar = findViewById(R.id.progressBar3)
        progressBar.visibility = View.VISIBLE

        val intent = intent
        val action = intent.action
        val uri: Uri? = intent.data
        val file: File?
        val navigator: MerakiNavigator by inject()

        if (action?.compareTo(Intent.ACTION_VIEW) == 0) {
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
                    scheme.compareTo("https")  == 0 -> {
                       compareToCommon(uri)
                    }
                    scheme.compareTo("http") == 0 -> {
                        compareToCommon(uri)
                    }
                    scheme.compareTo("ftp") == 0 -> {
                        // TODO Import from FTP!
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.READ_CONTACTS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.WRITE_CONTACTS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                recreate()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Some functionality may not work properly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun compareToCommon(uri : Uri?) {
        val url = uri!!.path.toString().removePrefix("/project/")
        val s3Url = "https://chanakya-dev.s3.ap-south-1.amazonaws.com/scratch/$url.sb3"
        val newIntent = Intent(this, ScratchActivity::class.java)
        newIntent.putExtra("s3Url", s3Url)
        startActivity(newIntent)
        finish()
    }

    @SuppressLint("Recycle")
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
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val returnCursor: Cursor? =
                    context.contentResolver.query(contentUri, null, null, null, null)

                returnCursor?.use {
                    if (it.moveToFirst()) {
                        val nameColumnIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameColumnIndex != -1) {
                            return it.getString(nameColumnIndex)
                        } else {
                            return ""
                        }
                    } else {
                        ""
                    }
                } ?: ""
            } else {
                ""
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            ""
        }
    }


    private fun getFileExtension(context: Context, contentUri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(contentUri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    private fun getExtensionFromName(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf(".")

        return if (lastDotIndex != -1 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1)
        } else {
            "IllegalArgumentException(\"Invalid file name: $fileName\")"
        }
    }


}