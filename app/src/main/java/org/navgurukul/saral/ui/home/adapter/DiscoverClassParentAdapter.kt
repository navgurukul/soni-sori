package org.navgurukul.saral.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import org.navgurukul.learn.ui.common.DataBoundListAdapter
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.ItemDiscoverClassParentBinding
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.util.DateTimeUtil
import java.util.*


@Suppress("UNCHECKED_CAST")
class DiscoverClassParentAdapter(callback: (ClassesContainer.Classes) -> Unit) :
    DataBoundListAdapter<DiscoverClassParentAdapter.DiscoverData, ItemDiscoverClassParentBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<DiscoverData>() {
            override fun areItemsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: DiscoverData, newItem: DiscoverData): Boolean {
                return false
            }
        }
    ), Filterable {
    private val mCallback = callback
    private var parentList: List<ClassesContainer.Classes> = mutableListOf()

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemDiscoverClassParentBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_discover_class_parent, parent, false
        )
    }

    override fun bind(binding: ItemDiscoverClassParentBinding, item: DiscoverData) {
        binding.date = DateTimeUtil.stringToDate(item.date)
        initChildAdapter(item.data, binding)
    }

    private fun initChildAdapter(
        data: List<ClassesContainer.Classes>,
        binding: ItemDiscoverClassParentBinding
    ) {
        val discoverClassChildAdapter = DiscoverClassChildAdapter {
            mCallback.invoke(it)
        }
        val layoutManager =
            GridLayoutManager(binding.root.context, 2)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = discoverClassChildAdapter
        discoverClassChildAdapter.submitList(data)
    }

    data class DiscoverData(var date: String?, var data: MutableList<ClassesContainer.Classes>)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val oReturn = FilterResults()
                if (parentList.isNotEmpty()) {
                    val results = parentList.filter {
                        it.title?.toLowerCase(Locale.ROOT)!!.contains(
                            constraint.toString().toLowerCase(
                                Locale.ROOT
                            )
                        )
                    }
                    oReturn.values = results
                }
                return oReturn
            }

            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                if (constraint.isEmpty())
                    submitList(parseData(parentList.toMutableList()))
                else
                    submitList(parseData(results.values as MutableList<ClassesContainer.Classes?>))
            }
        }
    }

    private fun parseData(it: MutableList<ClassesContainer.Classes?>): MutableList<DiscoverData>? {
        val data = it.groupBy {
            it?.startTime
        }
        val list = mutableListOf<DiscoverData>()
        data.forEach {
            list.add(
                DiscoverData(
                    it.key,
                    it.value as MutableList<ClassesContainer.Classes>
                )
            )
        }
        return list
    }

    fun submitData(it: MutableList<ClassesContainer.Classes?>) {
        parentList = it as MutableList<ClassesContainer.Classes>
        val parseData = parseData(it as MutableList<ClassesContainer.Classes?>)
        submitList(parseData)
        notifyDataSetChanged()
    }

}