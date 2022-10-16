package org.merakilearn.scratch.repo

import android.util.Base64
import java.io.*

class ScratchRepositoryImpl : ScratchRepository {

    companion object {
        const val DIRECTORY_NAME = "Scratch"
    }

//    override fun saveScratchFile(base64Str: String, fileName: String) {
//        var bos: BufferedOutputStream? = null
//        var fos: FileOutputStream? = null
//        var file: File? = null
//        try {
//            val dir = File(filePath)
//            if (!dir.exists() && dir.isDirectory) {
//                dir.mkdirs()
//            }
//            file = File(filePath, fileName)
//            fos = FileOutputStream(file)
//            bos = BufferedOutputStream(fos)
//            val bfile = Base64.decode(base64Str, Base64.DEFAULT)
//            bos.write(bfile)
//            println("File Saved")
//
//        } catch (e: FileNotFoundException) {
//            throw e
//        } catch (e: IOException) {
//            throw e
//        } finally {
//            if (bos != null) {
//                try {
//                    bos.close()
//                } catch (e1: IOException) {
//                    e1.printStackTrace()
//                }
//            }
//            if (fos != null) {
//                try {
//                    fos.close()
//                } catch (e1: IOException) {
//                    e1.printStackTrace()
//                }
//            }
//        }
//    }
}