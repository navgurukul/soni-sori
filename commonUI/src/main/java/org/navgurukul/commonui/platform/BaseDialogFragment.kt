package org.navgurukul.commonui.platform

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.google.android.play.core.splitcompat.SplitCompat
import timber.log.Timber

abstract class BaseDialogFragment: DialogFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (shouldInstallDynamicModule()) {
            SplitCompat.installActivity(requireContext())
        }
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("onCreateView Fragment ${this.javaClass.simpleName}")
        return inflater.inflate(getLayoutResId(), container, false)
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    open fun shouldInstallDynamicModule() = false
}