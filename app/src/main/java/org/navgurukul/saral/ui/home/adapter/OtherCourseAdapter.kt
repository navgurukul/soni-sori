package org.navgurukul.saral.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.ItemRecommendedClassBinding
import org.navgurukul.saral.datasource.network.model.RecommendedClass


class OtherCourseAdapter(callback: (Pair<RecommendedClass, ItemRecommendedClassBinding>) -> Unit) :

    DataBoundListAdapter<RecommendedClass, ItemRecommendedClassBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<RecommendedClass>() {
            override fun areItemsTheSame(oldItem: RecommendedClass, newItem: RecommendedClass): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: RecommendedClass, newItem: RecommendedClass): Boolean {
                return false
            }
        }
    ) {
    private val mCallback = callback
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemRecommendedClassBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_recommended_class, parent, false
        )
    }

    override fun bind(binding: ItemRecommendedClassBinding, item: RecommendedClass) {
        binding.course = item
        binding.root.setOnClickListener {
            mCallback.invoke(Pair(item, binding))
        }
    }

}