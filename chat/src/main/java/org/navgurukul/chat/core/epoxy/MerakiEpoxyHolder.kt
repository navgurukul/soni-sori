package org.navgurukul.chat.core.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A pattern for easier view binding with an [EpoxyHolder]
 *
 * See [SampleKotlinModelWithHolder] for a usage example.
 */
abstract class MerakiEpoxyHolder : EpoxyHolder() {
    lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView
    }

    protected fun <V : View> bind(id: Int): ReadOnlyProperty<MerakiEpoxyHolder, V> =
            Lazy { holder: MerakiEpoxyHolder, prop ->
                holder.view.findViewById(id) as V?
                ?: throw IllegalStateException("View ID $id for '${prop.name}' not found.")
            }

    protected fun <V : View?> bindNullable(id: Int): ReadOnlyProperty<MerakiEpoxyHolder, V> =
        Lazy { holder: MerakiEpoxyHolder, _ ->
            holder.view.findViewById<V>(id)
        }

    /**
     * Taken from Kotterknife.
     * https://github.com/JakeWharton/kotterknife
     */
    private class Lazy<V>(
            private val initializer: (MerakiEpoxyHolder, KProperty<*>) -> V
    ) : ReadOnlyProperty<MerakiEpoxyHolder, V> {
        private object EMPTY

        private var value: Any? = EMPTY

        override fun getValue(thisRef: MerakiEpoxyHolder, property: KProperty<*>): V {
            if (value == EMPTY) {
                value = initializer(thisRef, property)
            }
            @Suppress("UNCHECKED_CAST")
            return value as V
        }
    }
}