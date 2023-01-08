//package org.navgurukul.webide.git
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.AsyncTask
//import android.os.Build
//import android.view.View
//import androidx.core.app.NotificationCompat
//import io.geeteshk.webIDE.R
//import org.eclipse.jgit.lib.BatchingProgressMonitor
//import java.io.File
//import java.lang.ref.WeakReference
//
//abstract class GitTask(var context: WeakReference<Context>, var rootView: WeakReference<View>,
//                       var repo: File, private var messages: Array<String>)
//    : AsyncTask<String, String, Boolean>() {
//
//    val progressMonitor = object : BatchingProgressMonitor() {
//
//        override fun onUpdate(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
//            publishProgress(taskName, percentDone.toString(), workCurr.toString(), workTotal.toString())
//        }
//
//        override fun onEndTask(taskName: String?, workCurr: Int, workTotal: Int, percentDone: Int) {
//            publishProgress(taskName, workCurr.toString(), workTotal.toString())
//        }
//
//        override fun onUpdate(taskName: String?, workCurr: Int) {}
//        override fun onEndTask(taskName: String?, workCurr: Int) {}
//    }
//
//    var manager: NotificationManager = context.get()!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    var builder: NotificationCompat.Builder
//    var id = 1
//
//    init {
//
//        val id = "webIDE_git_channel"
//        if (Build.VERSION.SDK_INT >= 26) {
//            val name = context.get()!!.getString(R.string.app_name)
//            val description = context.get()!!.getString(R.string.git)
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(id, name, importance)
//            channel.description = description
//            manager.createNotificationChannel(channel)
//        }
//
//        builder = NotificationCompat.Builder(context.get()!!, id)
//    }
//
//    override fun onPreExecute() {
//        super.onPreExecute()
//        builder.setContentTitle(messages[0])
//                .setSmallIcon(R.drawable.ic_git_small)
//                .setAutoCancel(false)
//                .setOngoing(true)
//    }
//
//    override fun onProgressUpdate(vararg values: String) {
//        super.onProgressUpdate(*values)
//        builder.setContentText(values[0])
//                .setProgress(Integer.valueOf(values[2]), Integer.valueOf(values[1]), false)
//        builder.setStyle(NotificationCompat.BigTextStyle().bigText(values[0]))
//        manager.notify(id, builder.build())
//    }
//
//    override fun onPostExecute(aBoolean: Boolean?) {
//        super.onPostExecute(aBoolean)
//        builder.setContentText(messages[if (aBoolean!!) { 1 } else { 2 }])
//        builder.setProgress(0, 0, false)
//                .setAutoCancel(true)
//                .setOngoing(false)
//        manager.notify(id, builder.build())
//    }
//}
