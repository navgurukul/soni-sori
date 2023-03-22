package org.merakilearn.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import org.merakilearn.R
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.chat.core.glide.GlideApp

class OnBoardPagesAdapter(
    private val onBoardingData: OnBoardingData,
    private val onBoardingTranslations: OnBoardingTranslations,
    private val context: Context
) :
    RecyclerView.Adapter<OnBoardPageViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardPageViewHolder {
        return OnBoardPageViewHolder(
            layoutInflater.inflate(R.layout.on_board_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardPageViewHolder, position: Int) {
        val translation = onBoardingTranslations.onBoardingPageDataListTexts[position]
        holder.header.text = translation.header
        holder.desc.text = translation.description

        val onBoardingData = onBoardingData.onBoardingPagesList[position]

        onBoardingData.image.remote?.let {
            GlideApp.with(context)
                .load(it)
                .into(holder.imageView)
        } ?: run {
            holder.imageView.setImageResource(Images.valueOf(onBoardingData.image.local!!).id)
        }
    }

    override fun getItemCount(): Int {
        return onBoardingData.onBoardingPagesList.size
    }


    enum class Images(@DrawableRes val id: Int) {
        PYTHON(R.drawable.on_boarding_mobile),
        INTERVIEW(R.drawable.on_boarding_develop),
        INTERACTIVE(R.drawable.on_boarding_interactive_classes),
    }
}

class OnBoardPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: ImageView = view.findViewById(R.id.image)
    val header: TextView = view.findViewById(R.id.header)
    val desc: TextView = view.findViewById(R.id.desc)
}

