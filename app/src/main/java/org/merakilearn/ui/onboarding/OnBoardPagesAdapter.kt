package org.merakilearn.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.JsonClass
import org.merakilearn.R
import java.io.Serializable

class OnBoardPagesAdapter(private val list: List<OnBoardingData>):RecyclerView.Adapter<OnBoardPagesAdapter.OnBoardPageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardPageViewHolder {
        return OnBoardPageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.on_board_page_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardPageViewHolder, position: Int) {
        holder.header.text=list[position].header
        holder.desc.text= list[position].description

    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class OnBoardPageViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView: ImageView =view.findViewById(R.id.image)
        val header:TextView=view.findViewById(R.id.header)
        val desc:TextView=view.findViewById(R.id.desc)
    }


    @JsonClass(generateAdapter = true)
    data class OnBoardingPageData(
        val skip_text:String,
        val next_text:String,
        val skip_login_text:String,
        val login_with_google_text:String,
        val onBoardingDataList:List<OnBoardingData>,
        val onBoardingPathwayList:List<PathwayData>,
        val select_course_header:String
    )

    @JsonClass(generateAdapter = true)
    data class OnBoardingData(
        val image:String,
        val header:String,
        val description:String
    )

    @JsonClass(generateAdapter = true)
    data class PathwayData(
        val id:String,
        val logo:String,
        val name:String
    ):Serializable
}