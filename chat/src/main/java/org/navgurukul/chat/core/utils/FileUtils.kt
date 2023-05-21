package org.navgurukul.chat.core.utils

import timber.log.Timber
import java.io.File

internal fun String?.isLocalFile() = this != null && File(this).exists()

// Implementation should return true in case of success
typealias ActionOnFile = (file: File) -> Boolean

/* ==========================================================================================
 * Delete
 * ========================================================================================== */

fun deleteAllFiles(root: File) {
    Timber.v("Delete ${root.absolutePath}")
    recursiveActionOnFile(root, ::deleteAction)
}

private fun deleteAction(file: File): Boolean {
    if (file.exists()) {
        Timber.v("deleteFile: $file")
        return file.delete()
    }

    return true
}

/**
 * Get the file extension of a fileUri or a filename
 *
 * @param fileUri the fileUri (can be a simple filename)
 * @return the file extension, in lower case, or null is extension is not available or empty
 */
fun getFileExtension(fileUri: String): String? {
    var reducedStr = fileUri

    if (reducedStr.isNotEmpty()) {
        // Remove fragment
        reducedStr = reducedStr.substringBeforeLast('#')

        // Remove query
        reducedStr = reducedStr.substringBeforeLast('?')

        // Remove path
        val filename = reducedStr.substringAfterLast('/')

        // Contrary to method MimeTypeMap.getFileExtensionFromUrl, we do not check the pattern
        // See https://stackoverflow.com/questions/14320527/android-should-i-use-mimetypemap-getfileextensionfromurl-bugs
        if (filename.isNotEmpty()) {
            val dotPos = filename.lastIndexOf('.')
            if (0 <= dotPos) {
                val ext = filename.substring(dotPos + 1)

                if (ext.isNotBlank()) {
                    return ext.toLowerCase()
                }
            }
        }
    }

    return null
}

/* ==========================================================================================
 * Private
 * ========================================================================================== */

/**
 * Return true in case of success
 */
private fun recursiveActionOnFile(file: File, action: ActionOnFile): Boolean {
    if (file.isDirectory) {
        file.list()?.forEach {
            val result = recursiveActionOnFile(File(file, it), action)

            if (!result) {
                // Break the loop
                return false
            }
        }
    }

    return action.invoke(file)
}