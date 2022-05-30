package org.navgurukul.learn.ui.learn

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.OutputBaseCourseContent
import org.navgurukul.learn.ui.learn.viewholder.BaseCourseViewHolder

class OutputCourseViewHolder(itemView: View):
BaseCourseViewHolder(itemView){
    private val layout: ConstraintLayout = populateStub(R.layout.item_output_content)
    private val outputView: RecyclerView = layout.findViewById(R.id.outputLayout)

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: OutputBaseCourseContent) {
        super.bind(item)

        item.value.correct.let {
            if (it.isNotEmpty()){
//                val adapter = OutputAdapter(it)
//                val layoutManager = LinearLayoutManager(outputView.context ,LinearLayoutManager.VERTICAL, false)
//                outputView.layoutManager = layoutManager
//
//                outputView.adapter = adapter
//                outputView.addItemDecoration(
//                    ListSpacingDecoration(
//                        outputView.context,
//                        R.dimen.table_margin_vertical_spacing,
//                        R.dimen.table_margin_horizontal_spacing
//                    )
//                )
            }
        }
    }

//    private fun getFlattenedTableList(list: List<BaseCourseContent>): List<String> {
//        val flatList = ArrayList<String>()
//        for (item in list) {
//            flatList.add(item.header ?: "")
//            item.items?.let { flatList.addAll(it) }
//        }
//        return flatList
//    }

}