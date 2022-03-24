package org.navgurukul.learn.ui.learn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_upcoming_class.view.*
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.UpcomingClass

class UpcomingClassAdapter: RecyclerView.Adapter<UpcomingClassAdapter.MyViewHolder>()  {
    private var upcomingClasses : List<UpcomingClass>? = null

    fun setUpcomingClasses(upcomingClasses : List<UpcomingClass>?){
        this.upcomingClasses= upcomingClasses
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UpcomingClassAdapter.MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingClassAdapter.MyViewHolder, position: Int) {
       holder.bind(upcomingClasses?.get(position)!!)
    }

    override fun getItemCount(): Int {
        if (upcomingClasses == null) return 0
        else return upcomingClasses?.size!!
    }

    class MyViewHolder(view :View): RecyclerView.ViewHolder(view){

        val subTitle = view.sub_title
        val tvTitle = view.tvTitle

        fun bind(data : UpcomingClass) {
            subTitle.text = data.sub_title
            tvTitle.text = data.type

        }
    }
}