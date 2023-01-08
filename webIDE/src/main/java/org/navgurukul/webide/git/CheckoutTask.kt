//package org.navgurukul.webide.git
//
//import android.app.Activity
//import android.content.Context
//import android.view.View
//import org.eclipse.jgit.api.errors.GitAPIException
//import timber.log.Timber
//import java.io.File
//import java.lang.ref.WeakReference
//
//class CheckoutTask internal constructor(context: WeakReference<Context>, view: WeakReference<View>, repo: File, values: Array<String>) : GitTask(context, view, repo, values) {
//
//    init {
//        id = 2
//    }
//
//    override fun doInBackground(vararg strings: String): Boolean? {
//        try {
//            val git = GitWrapper.getGit(rootView.get()!!, repo)
//            git?.checkout()?.setCreateBranch(strings[0].toBoolean())?.setName(strings[1])?.call()
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
//        super.onPostExecute(aBoolean)
//        if (aBoolean!!) (context.get() as Activity).finish()
//    }
//}
