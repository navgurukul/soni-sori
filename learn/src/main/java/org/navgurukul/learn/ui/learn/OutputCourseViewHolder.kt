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
//                val
            }
        }
    }

}