package org.merakilearn.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import org.merakilearn.R
import org.merakilearn.datasource.network.model.OnBoardingData
import org.navgurukul.chat.core.glide.GlideApp

class OnBoardPagesAdapter(private val list: List<OnBoardingData>,private val context:Context):RecyclerView.Adapter<OnBoardPagesAdapter.OnBoardPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardPageViewHolder {
        return OnBoardPageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.on_board_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardPageViewHolder, position: Int) {
        holder.header.text = list[position].header
        holder.desc.text = list[position].description


        val thumbnail=GlideApp.with(context)
            .load(Images.values()[position])

        GlideApp.with(context)
            .load(list[position].image)
            .thumbnail(thumbnail)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class OnBoardPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
        val header: TextView = view.findViewById(R.id.header)
        val desc: TextView = view.findViewById(R.id.desc)
    }


    enum class Images(@DrawableRes val id:Int){
        PYTHON(R.drawable.on_boarding_learn_python),
        INTERVIEW(R.drawable.on_boarding_job_interview),
        INTERACTIVE_CLASSES(R.drawable.on_boarding_book_lover)
    }


}

