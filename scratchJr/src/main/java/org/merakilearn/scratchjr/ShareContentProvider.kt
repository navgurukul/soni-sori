package org.merakilearn.scratchjr

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException


// Special thanks to stephendnicholas.com for a reference implementation
class ShareContentProvider : ContentProvider() {
    override fun getType(uri: Uri): String? {
        if (BuildConfig.APPLICATION_ID == "org.pbskids.scratchjr") {
            return "application/x-pbskids-scratchjr-project"
        }
        return "application/x-scratchjr-project"
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        // Provide a read-only file descriptor for the shared file
        val fileLocation = (context!!.cacheDir.toString() + File.separator
                + uri.lastPathSegment)
        val pfd = ParcelFileDescriptor.open(
            File(
                fileLocation
            ), ParcelFileDescriptor.MODE_READ_ONLY
        )
        return pfd
    }

    // Unimplemented methods since we're only providing a file reference
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return -1
    }

    companion object {
        const val AUTHORITY: String = BuildConfig.APPLICATION_ID + ".ShareContentProvider"
    }
}