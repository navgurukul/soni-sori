package org.merakilearn.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.merakilearn.R
import org.merakilearn.databinding.ItemSavedFileBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import java.io.File


class SavedFileAdapter(var callback: ((File, View) -> Unit)? = null) :

    DataBoundListAdapter<File, ItemSavedFileBinding>(
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
        item: File
    ) {
        holder.binding.tvSavedFile.text = item.name.replaceAfterLast("_", "").removeSuffix("_")
        holder.binding.root.setOnClickListener {
            callback?.invoke(item, it)
        }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<File>() {
                override fun areItemsTheSame(
                    oldItem: File,
                    newItem: File
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: File,
                    newItem: File
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}