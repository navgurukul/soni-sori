package org.merakilearn.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.merakilearn.R
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.datasource.network.model.displayableLanguage
import org.merakilearn.datasource.network.model.sanitizedType
import org.merakilearn.datasource.network.model.timeRange
import org.navgurukul.commonui.platform.BaseViewHolder


class ClassesAdapter(
    context: Context,
    val callback: (Classes) -> Unit,
) :
    ListAdapter<ClassesItemContainer, BaseViewHolder<ClassesItemContainer>>(DIFF_UTIL) {

    private val layoutInflater = LayoutInflater.from(context)

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<ClassesItemContainer>() {
            override fun areItemsTheSame(
                oldItem: ClassesItemContainer,
                newItem: ClassesItemContainer
            ): Boolean {
                return oldItem.areItemsSame(newItem)
            }

            override fun areContentsTheSame(
                oldItem: ClassesItemContainer,
                newItem: ClassesItemContainer
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ClassesItemContainer> {
        return when (viewType) {
            ClassesItemTypes.CLASS.ordinal -> {
                ClassViewHolder(
                    layoutInflater.inflate(R.layout.item_class, parent, false),
                    callback
                )
            }
            ClassesItemTypes.HEADER.ordinal -> {
                ClassesHeaderHolder(
                    layoutInflater.inflate(
                        R.layout.item_class_header,
                        parent,
                        false

                    )
                )
            }
            else -> object : BaseViewHolder<ClassesItemContainer>(View(parent.context)) {}
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ClassesItemContainer>, position: Int) {
        holder.onBind(getItem(position))
    }
}

class ClassViewHolder(
    view: View,
    val callback: (Classes) -> Unit,
) : BaseViewHolder<ClassesItemContainer>(view) {

    private val titleView = itemView.findViewById<TextView>(R.id.tvClassTitle)
    private val tvClassDescription = itemView.findViewById<TextView>(R.id.tvClassDescription)
    private val tvClassLanguage = itemView.findViewById<TextView>(R.id.tvClassLanguage)
    private val tvClassTiming = itemView.findViewById<TextView>(R.id.tvClassTiming)
    private val tvClassEnrolled = itemView.findViewById<TextView>(R.id.tvClassEnrolled)

    override fun onBind(model: ClassesItemContainer) {
        val classes = model.data as Classes

        tvClassDescription.text = classes.title
        titleView.text = classes.sanitizedType()
        tvClassTiming.text = classes.timeRange()
        tvClassEnrolled.isVisible = classes.enrolled
        tvClassLanguage.text = classes.displayableLanguage()

        itemView.setOnClickListener {
            callback(classes)
        }
    }
}

class ClassesHeaderHolder(view: View) :
    BaseViewHolder<ClassesItemContainer>(view) {

    private val headerView = itemView.findViewById<TextView>(R.id.tvDate)

    override fun onBind(model: ClassesItemContainer) {
        val header = model.data as ClassesHeader
        headerView.text = header.title
    }
}