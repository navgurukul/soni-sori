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
//class PushTask internal constructor(context: WeakReference<Context>, view: WeakReference<View>, repo: File, values: Array<String>, private val gitOptions: BooleanArray) : GitTask(context, view, repo, values) {
//
//    init {
//        id = 6
//    }
//
//    override fun doInBackground(vararg params: String): Boolean? {
//        val git = GitWrapper.getGit(rootView.get()!!, repo)
//        git?.let {
//            try {
//                if (gitOptions[3]) {
//                    git.push()
//                            .setRemote(params[0])
//                            .setDryRun(gitOptions[0])
//                            .setForce(gitOptions[1])
//                            .setThin(gitOptions[2])
//                            .setPushTags()
//                            .setCredentialsProvider(UsernamePasswordCredentialsProvider(params[1], params[2]))
//                            .setProgressMonitor(progressMonitor)
//                            .call()
//                } else {
//                    git.push()
//                            .setRemote(params[0])
//                            .setDryRun(gitOptions[0])
//                            .setForce(gitOptions[1])
//                            .setThin(gitOptions[2])
//                            .setCredentialsProvider(UsernamePasswordCredentialsProvider(params[1], params[2]))
//                            .setProgressMonitor(progressMonitor)
//                            .call()
//                }
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
