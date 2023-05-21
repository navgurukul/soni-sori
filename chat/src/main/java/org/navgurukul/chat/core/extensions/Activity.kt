package org.navgurukul.chat.core.extensions

import android.app.Activity
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import org.merakilearn.core.extentions.hideKeyboard
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.commonui.platform.BaseFragment

fun FragmentActivity.addFragment(frameId: Int, fragment: Fragment) {
    supportFragmentManager.commitTransaction { add(frameId, fragment) }
}

fun <T : BaseFragment> FragmentActivity.addFragment(frameId: Int, fragmentClass: Class<T>, params: Parcelable? = null, tag: String? = null) {
    supportFragmentManager.commitTransaction {
        add(frameId, fragmentClass, params.toBundle(), tag)
    }
}

fun FragmentActivity.replaceFragment(frameId: Int, fragment: Fragment, tag: String? = null) {
    supportFragmentManager.commitTransaction { replace(frameId, fragment, tag) }
}

fun <T : Fragment> FragmentActivity.replaceFragment(frameId: Int, fragmentClass: Class<T>, params: Parcelable? = null, tag: String? = null) {
    supportFragmentManager.commitTransaction {
        replace(frameId, fragmentClass, params.toBundle(), tag)
    }
}

fun FragmentActivity.addFragmentToBackstack(frameId: Int, fragment: Fragment, tag: String? = null) {
    supportFragmentManager.commitTransaction { replace(frameId, fragment).addToBackStack(tag) }
}

fun <T : Fragment> FragmentActivity.addFragmentToBackstack(frameId: Int,
                                                             fragmentClass: Class<T>,
                                                             params: Parcelable? = null,
                                                             tag: String? = null,
                                                             option: ((FragmentTransaction) -> Unit)? = null) {
    supportFragmentManager.commitTransaction {
        option?.invoke(this)
        replace(frameId, fragmentClass, params.toBundle(), tag).addToBackStack(tag)
    }
}

fun FragmentActivity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}

fun Activity.restart() {
    startActivity(intent)
    finish()
}