package org.merakilearn.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemSavedFileBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter


class SavedFileAdapter(var callback: ((Triple<String, String, View>) -> Unit)? = null) :

    DataBoundListAdapter<Pair<String, String>, ItemSavedFileBinding>(
        mDiffCallback = DIFF_CALLBACK
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSavedFileBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_saved_file, parent, false
        )
    }

    override fun bind(
        holder: DataBoundViewHolder<ItemSavedFileBinding>,
        item: Pair<String, String>
    ) {
        holder.binding.tvSavedFile.text = item.second
        holder.binding.root.setOnClickListener {
            callback?.invoke(Triple(item.first,item.second,holder.binding.root))
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Pair<String, String>>() {
                override fun areItemsTheSame(
                    oldItem: Pair<String, String>,
                    newItem: Pair<String, String>
                ): Boolean {
                    return oldItem.first == newItem.first
                }

                override fun areContentsTheSame(
                    oldItem: Pair<String, String>,
                    newItem: Pair<String, String>
                ): Boolean {
                    return oldItem.second == newItem.second
                }
            }
    }
}