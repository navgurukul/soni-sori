package org.navgurukul.learn.ui.learn.c4ca

import android.R.attr.data
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.databinding.ItemModuleBinding


class C4CAAdapter :

    ListAdapter<Module, C4CAAdapter.C4CAViewHolder>(ModulesDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): C4CAViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemModuleBinding.inflate(layoutInflater, parent, false)
        return C4CAViewHolder(binding)
    }

    override fun onBindViewHolder(holder: C4CAViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.bind(getItem(position))
    }

    inner class C4CAViewHolder(
        val binding: ItemModuleBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Module) {
            binding.tvModule.text = item.name
            binding.llModule.setBackgroundColor(item.color?.let {
                Color.parseColor(it) } ?: Color.parseColor("#ffff00"))
        }
    }

    private class ModulesDC : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(
            oldItem: Module,
            newItem: Module
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Module,
            newItem: Module
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
}