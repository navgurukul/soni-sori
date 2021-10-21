package org.navgurukul.commonui.platform

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.navgurukul.commonui.R
import org.navgurukul.commonui.error.ErrorFormatter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty1

abstract class BaseFragment : Fragment() {

    protected val errorFormatter: ErrorFormatter by inject()

    //TODO remove with custom progress dialog
    private var progress: ProgressDialog? = null

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

    open fun showLoading(message: CharSequence? = null) {
        showLoadingDialog(message)
    }

    open fun showFailure(throwable: Throwable) {
        displayErrorDialog(throwable)
    }


    protected fun showLoadingDialog(message: CharSequence? = null, cancelable: Boolean = false) {
        progress = ProgressDialog(requireContext()).apply {
            setCancelable(cancelable)
            setMessage(message ?: getString(R.string.please_wait))
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            show()
        }
        lifecycle.addObserver( DialogDismissLifecycleObserver(progress) )
    }

    protected fun dismissLoadingDialog() {
        progress?.dismiss()
        progress = null
    }

    /**
     * Accesses ViewModel state from a single ViewModel synchronously and returns the result of the block.
     */
    fun <A : BaseViewModel<*, B>, B : ViewState, C> withState(viewModel: A, block: (B) -> C) =
        block(viewModel.viewState.value!!)

    fun <S : ViewState, A, B> BaseViewModel<*, S>.selectSubscribe(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>
    ): LiveData<Pair<A, B>> = viewState.map {
        Pair(prop1.get(it), prop2.get(it))
    }.distinctUntilChanged()

    fun <S : ViewState, A> BaseViewModel<*, S>.selectSubscribe(
        prop1: KProperty1<S, A>
    ): LiveData<A> = viewState.map {
        prop1.get(it)
    }.distinctUntilChanged()


    protected fun displayErrorDialog(throwable: Throwable) {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.dialog_title_error)
            .setMessage(errorFormatter.toHumanReadable(throwable))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    /* ==========================================================================================
     * Disposable
     * ========================================================================================== */

    private val uiDisposables = CompositeDisposable()

    protected fun Disposable.disposeOnDestroyView() {
        uiDisposables.add(this)
    }

    /* ==========================================================================================
     * Toolbar
     * ========================================================================================== */

    /**
     * Configure the Toolbar.
     */
    protected fun setupToolbar(toolbar: Toolbar) {
        val parentActivity = activity
        if (parentActivity is ToolbarConfigurable) {
            parentActivity.configure(toolbar)
        }
    }

    /* ==========================================================================================
     * Views
     * ========================================================================================== */

    protected fun View.debouncedClicks(onClicked: () -> Unit) {
        clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { onClicked() }
            .disposeOnDestroyView()
    }

    protected fun showErrorInSnackbar(throwable: Throwable) {
        activity?.findViewById<View>(R.id.root_layout)?.let {
            Snackbar.make(it, errorFormatter.toHumanReadable(throwable), Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}

class DialogDismissLifecycleObserver( private var dialog: Dialog? ) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        dialog?.dismiss()
        dialog = null
    }
}