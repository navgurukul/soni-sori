package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.TableBaseCourseContent
import org.navgurukul.learn.courses.db.models.TableColumn
import org.navgurukul.learn.ui.learn.adapter.ListSpacingDecoration
import org.navgurukul.learn.ui.learn.adapter.TableAdapter

class TableCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val tableView: RecyclerView = populateStub(R.layout.item_table_content)

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
                        R.dimen.table_margin_offset
                    )
                )

                tableAdapter.notifyDataSetChanged()
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
