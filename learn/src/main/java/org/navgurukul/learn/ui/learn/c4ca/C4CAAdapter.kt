package org.navgurukul.learn.ui.learn.c4ca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_category.view.tv_category
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.databinding.ItemCategoryBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class C4CAAdapter(private val module: List<Module>) :

    RecyclerView.Adapter<C4CAAdapter.C4CAViewHolder>() {

    inner class C4CAViewHolder(val itemView: ItemCategoryBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item: Module) {
            itemView.tv_category.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): C4CAViewHolder {
        val view = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return C4CAViewHolder(view)
    }

    override fun getItemCount(): Int {
        return module.size
    }

    override fun onBindViewHolder(holder: C4CAViewHolder, position: Int) {
        holder.bind(module[position])
    }

    fun addData(list: List<Module>) {
        module.toMutableList().addAll(list)
    }
}