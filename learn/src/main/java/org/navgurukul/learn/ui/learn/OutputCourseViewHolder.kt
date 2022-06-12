package org.navgurukul.learn.ui.learn

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.OutputBaseCourseContent
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter
import org.navgurukul.learn.ui.learn.viewholder.BaseCourseViewHolder

class OutputCourseViewHolder(itemView: View):
BaseCourseViewHolder(itemView){
    private val layout: ConstraintLayout = populateStub(R.layout.item_output_content)
    private val outputView: RecyclerView = layout.findViewById(R.id.outputLayout)
    private lateinit var mClassAdapter: ExerciseContentAdapter

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: OutputBaseCourseContent) {
        super.bind(item)

        item.value.correct.let {
            if (it.isNotEmpty()){
                mClassAdapter =  ExerciseContentAdapter(this.requirecontext,{

                }){}
                val layoutManager = LinearLayoutManager(outputView.context, LinearLayoutManager.VERTICAL,false)
                outputView.layoutManager = layoutManager
                outputView.adapter = mClassAdapter
                mClassAdapter.submitList(it)
            }
        }
    }


}