package org.navgurukul.learn.ui.common

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding
</V></T> */
abstract class DataBoundListAdapter<T, V : ViewDataBinding>(
    mDiffCallback: DiffUtil.ItemCallback<T>,
    private val viewHolderCreator: ((V) -> DataBoundViewHolder<V>)? = null
) : ListAdapter<T, DataBoundListAdapter.DataBoundViewHolder<V>>(
        mDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent, viewType)
        return viewHolderCreator?.invoke(binding) ?: DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        bind(holder, getItem(position))
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(holder: DataBoundViewHolder<V>, item: T)

    open class DataBoundViewHolder<out T : ViewDataBinding> constructor(val binding: T) :
        RecyclerView.ViewHolder(binding.root)
}
