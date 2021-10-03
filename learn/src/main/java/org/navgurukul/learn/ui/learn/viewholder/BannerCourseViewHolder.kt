package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BannerAction
import org.navgurukul.learn.courses.db.models.BannerBaseCourseContent

class BannerCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val layoutBannerContent = populateStub<ConstraintLayout>(R.layout.item_banner_content)
    private val bannerTitle: TextView = layoutBannerContent.findViewById(R.id.bannerTitle)
    private val bannerBody: TextView = layoutBannerContent.findViewById(R.id.bannerBody)
    private val bannerButton2: Button = layoutBannerContent.findViewById(R.id.bannerButton2)
    private val bannerButton1: Button = layoutBannerContent.findViewById(R.id.bannerButton1)

    override val horizontalMargin: Int
        get() = layoutBannerContent.context.resources.getDimensionPixelOffset(R.dimen.dimen_course_content_margin)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: BannerBaseCourseContent, urlCallback: (BannerAction?) -> Unit) {
        super.bind(item)

        item.actions?.let {
            setActionButtons(it, urlCallback)
        }
        bannerTitle.text = item.title
        bannerBody.text = item.value
    }

    private fun setActionButtons(
        actions: List<BannerAction>,
        urlCallback: (BannerAction?) -> Unit
    ) {
        actions.forEachIndexed { index, element ->
            if (index == 0) {
                bannerButton2.visibility = View.GONE

                bannerButton1.visibility = View.VISIBLE
                bannerButton1.text = element.label
                bannerButton1.setOnClickListener {
                    urlCallback.invoke(element)
                }
            } else {
                bannerButton2.visibility = View.VISIBLE
                bannerButton2.text = element.label
                bannerButton2.setOnClickListener {
                    urlCallback.invoke(element)
                }
            }
        }
    }

}

