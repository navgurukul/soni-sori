//package org.navgurukul.webide.git
//
//import android.content.Context
//import android.view.View
//import io.geeteshk.webIDE.extensions.snack
//import org.eclipse.jgit.api.errors.GitAPIException
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
//import timber.log.Timber
//import java.io.File
//import java.lang.ref.WeakReference
//
//class PullTask internal constructor(context: WeakReference<Context>, view: WeakReference<View>, repo: File, values: Array<String>) : GitTask(context, view, repo, values) {
//
//    init {
//        id = 5
//    }
//
//    override fun doInBackground(vararg params: String): Boolean? {
//        val git = GitWrapper.getGit(rootView.get()!!, repo)
//        git?.let {
//            try {
//                git.pull()
//                        .setRemote(params[0])
//                        .setCredentialsProvider(UsernamePasswordCredentialsProvider(params[1], params[2]))
//                        .setProgressMonitor(progressMonitor)
//                        .call()
//            } catch (e: GitAPIException) {
//                Timber.e(e)
//                rootView.get()?.snack(e.toString())
//                return false
//            }
//
//            return true
//        }
//
//        return false
//    }
//}
