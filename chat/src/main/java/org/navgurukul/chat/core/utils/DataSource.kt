package org.navgurukul.chat.core.utils

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

interface DataSource<T> {
    fun observe(): Observable<T>
}

interface MutableDataSource<T> : DataSource<T> {
    fun post(value: T)
}

/**
 * This datasource emits the most recent value it has observed and all subsequent observed values to each subscriber.
 */
open class BehaviorDataSource<T>(private val defaultValue: T? = null) : MutableDataSource<T> {

    private val behaviorRelay = createRelay()

    val currentValue: T?
        get() = behaviorRelay.value

    override fun observe(): Observable<T> {
        return behaviorRelay.hide().observeOn(AndroidSchedulers.mainThread())
    }

    override fun post(value: T) {
        behaviorRelay.accept(value)
    }

    private fun createRelay(): BehaviorRelay<T> {
        return if (defaultValue == null) {
            BehaviorRelay.create<T>()
        } else {
            BehaviorRelay.createDefault(defaultValue)
        }
    }
}

/**
 * This datasource only emits all subsequent observed values to each subscriber.
 */
open class PublishDataSource<T> : MutableDataSource<T> {

    private val publishRelay = PublishRelay.create<T>()

    override fun observe(): Observable<T> {
        return publishRelay.hide().observeOn(AndroidSchedulers.mainThread())
    }

    override fun post(value: T) {
        publishRelay.accept(value)
    }
}
