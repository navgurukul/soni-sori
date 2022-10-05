package org.merakilearn.core.extentions

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val KEY_ARG = "saral:arg"

fun Parcelable?.toBundle(): Bundle? {
    return this?.let { Bundle().apply { putParcelable(KEY_ARG, it) } }
}

/**
 * Fragment argument delegate that makes it possible to set fragment args without
 * creating a key for each one.
 *
 * To create arguments, define a property in your fragment like:
 *     `private val listingId by arg<MyArgs>()`
 *
 * Each fragment can only have a single argument with the key [KEY_ARG]
 */
fun <V : Any> fragmentArgs() = object : ReadOnlyProperty<Fragment, V> {
    var value: V? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        if (value == null) {
            val args = thisRef.arguments ?: throw IllegalArgumentException("There are no fragment arguments!")
            val argUntyped = args.get(KEY_ARG)
            argUntyped ?: throw IllegalArgumentException("MvRx arguments not found at key MvRx.KEY_ARG!")
            @Suppress("UNCHECKED_CAST")
            value = argUntyped as V
        }
        return value ?: throw IllegalArgumentException("")
    }
}

/**
 * Fragment argument delegate that makes it possible to set fragment args without
 * creating a key for each one.
 *
 * To create arguments, define a property in your fragment like:
 *     `private val listingId by arg<MyArgs>()`
 *
 * Each fragment can only have a single argument with the key [MvRx.KEY_ARG]
 */
fun <V : Any> activityArgs() = object : ReadOnlyProperty<Activity, V> {
    var value: V? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): V {
        if (value == null) {
            val args = thisRef.intent.extras ?: throw IllegalArgumentException("There are no fragment arguments!")
            val argUntyped = args.get(KEY_ARG)
            argUntyped ?: throw IllegalArgumentException("MvRx arguments not found at key MvRx.KEY_ARG!")
            @Suppress("UNCHECKED_CAST")
            value = argUntyped as V
        }
        return value ?: throw IllegalArgumentException("")
    }
}