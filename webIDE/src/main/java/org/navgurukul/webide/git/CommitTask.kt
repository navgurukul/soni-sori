package org.navgurukul.webide.git

import android.content.Context
import android.view.View
import org.eclipse.jgit.api.errors.GitAPIException
import org.navgurukul.webide.extensions.snack
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

class CommitTask internal constructor(context: WeakReference<Context>, view: WeakReference<View>, repo: File, values: Array<String>) : GitTask(context, view, repo, values) {

    init {
        id = 4
    }

    override fun doInBackground(vararg strings: String): Boolean? {
        try {
            val git = GitWrapper.getGit(rootView.get()!!, repo)
            git?.commit()?.setMessage(strings[0])?.call()
        } catch (e: GitAPIException) {
            Timber.e(e)
            rootView.get()?.snack(e.toString())
            return false
        }

        return true
    }
}
