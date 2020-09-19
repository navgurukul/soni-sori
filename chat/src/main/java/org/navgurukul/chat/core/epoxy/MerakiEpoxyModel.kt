package org.navgurukul.chat.core.epoxy

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.VisibilityState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

/**
 * EpoxyModelWithHolder which can listen to visibility state change
 */
abstract class MerakiEpoxyModel<H : MerakiEpoxyHolder> : EpoxyModelWithHolder<H>(), LifecycleOwner {

    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycleRegistry

    private var onModelVisibilityStateChangedListener: OnVisibilityStateChangedListener? = null

    @CallSuper
    override fun bind(holder: H) {
        super.bind(holder)
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    @CallSuper
    override fun unbind(holder: H) {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        coroutineScope.coroutineContext.cancelChildren()
        super.unbind(holder)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: H) {
        onModelVisibilityStateChangedListener?.onVisibilityStateChanged(visibilityState)
        super.onVisibilityStateChanged(visibilityState, view)
    }

    fun setOnVisibilityStateChanged(listener: OnVisibilityStateChangedListener): MerakiEpoxyModel<H> {
        this.onModelVisibilityStateChangedListener = listener
        return this
    }

    interface OnVisibilityStateChangedListener {
        fun onVisibilityStateChanged(@VisibilityState.Visibility visibilityState: Int)
    }
}