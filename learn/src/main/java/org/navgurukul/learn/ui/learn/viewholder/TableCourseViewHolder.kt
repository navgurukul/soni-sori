package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.TableBaseCourseContent
import org.navgurukul.learn.courses.db.models.TableColumn
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.ui.learn.adapter.TableAdapter

class TableCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val layout: ConstraintLayout = populateStub(R.layout.item_table_content)
    private val tableView: RecyclerView = layout.findViewById(R.id.tableLayout)
    private val fadedView: View = layout.findViewById(R.id.fadedView)
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                fadedView.visibility = View.GONE
            }else{
                fadedView.visibility = View.VISIBLE
            }
        }
    }

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: TableBaseCourseContent) {
        super.bind(item)

        item.value?.let {

            if (it.isNotEmpty()) {
                val noOfRows = it[0].items?.size?.plus(1) ?: 0
                val tableAdapter = TableAdapter(noOfRows, getFlattenedTableList(it))

                tableView.layoutManager = GridLayoutManager(
                    tableView.context, noOfRows, GridLayoutManager.HORIZONTAL, false
                )
                tableView.adapter = tableAdapter
                tableView.addItemDecoration(
                    ListSpacingDecoration(
                        tableView.context,
                        R.dimen.table_margin_vertical_spacing,
                        R.dimen.table_margin_horizontal_spacing
                    )
                )

                tableAdapter.notifyDataSetChanged()

                tableView.postDelayed({
                    if(!tableView.canScrollHorizontally(1)) {
                        fadedView.visibility = View.GONE
                        tableView.removeOnScrollListener(scrollListener)
                    }
                    else {
                        fadedView.visibility = View.VISIBLE
                        tableView.addOnScrollListener(scrollListener)
                    }
                }, 200)
            }
        }
    }

    private fun getFlattenedTableList(list: List<TableColumn>): List<String> {
        val flatList = ArrayList<String>()
        for (item in list) {
            flatList.add(item.header ?: "")
            item.items?.let { flatList.addAll(it) }
        }
        return flatList
    }

}
