//package org.navgurukul.webide.git
//
//import android.content.Context
//import android.view.View
//import io.geeteshk.webIDE.extensions.snack
//import io.geeteshk.webIDE.ui.adapter.ProjectAdapter
//import io.geeteshk.webIDE.util.project.ProjectManager
//import org.eclipse.jgit.api.Git
//import org.eclipse.jgit.api.errors.GitAPIException
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
//import timber.log.Timber
//import java.io.File
//import java.lang.ref.WeakReference
//
//class CloneTask internal constructor(context: WeakReference<Context>, view: WeakReference<View>, repo: File, private val projectAdapter: ProjectAdapter) : GitTask(context, view, repo, arrayOf("Cloning repository", "", "")) {
//
//    init {
//        id = 3
//    }
//
//    override fun doInBackground(vararg strings: String): Boolean? {
//        try {
//            Git.cloneRepository()
//                    .setURI(strings[0])
//                    .setDirectory(repo)
//                    .setCredentialsProvider(UsernamePasswordCredentialsProvider(strings[1], strings[2]))
//                    .setProgressMonitor(progressMonitor)
//                    .call()
//        } catch (e: GitAPIException) {
//            Timber.e(e)
//            rootView.get()?.snack(e.toString())
//            return false
//        }
//
//        return true
//    }
//
//    override fun onPostExecute(aBoolean: Boolean?) {
//        if (aBoolean!!) {
//            if (!ProjectManager.isValid(repo.name)) {
//                builder.setContentText("The repo was successfully cloned but it doesn't seem to be a webIDE project.")
//            } else {
//                projectAdapter.insert(repo.path.substring(repo.path.lastIndexOf("/") + 1, repo.path.length))
//                builder.setContentText("Successfully cloned.")
//            }
//        } else {
//            builder.setContentText("Unable to clone repo.")
//        }
//
//        builder.setProgress(0, 0, false)
//        manager.notify(id, builder.build())
//    }
//}
