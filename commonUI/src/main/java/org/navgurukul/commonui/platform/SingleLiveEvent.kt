package org.navgurukul.commonui.platform

import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean

open class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val liveDataToObserve: LiveData<T>
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        liveDataToObserve.observe(owner, Observer { t: T ->
            if (mPending.get()) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mPending.set(true)
        super.postValue(value)
    }

    override fun removeObserver(observer: Observer<in T>) {
        liveDataToObserve.removeObserver(observer)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        liveDataToObserve.removeObservers(owner)
    }

    init {
        liveDataToObserve = MediatorLiveData<T>()
        liveDataToObserve.addSource(this) { currentValue: T ->
            liveDataToObserve.value = currentValue
            mPending.set(false)
        }
    }
}