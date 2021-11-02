package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import org.navgurukul.commonui.resources.ResourceResolver
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BannerAction
import org.navgurukul.learn.courses.db.models.BannerBaseCourseContent
import org.navgurukul.learn.ui.common.MerakiButton

class BannerCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val layoutBannerContent = populateStub<ConstraintLayout>(R.layout.item_banner_content)
    private val bannerTitle: TextView = layoutBannerContent.findViewById(R.id.bannerTitle)
    private val bannerBody: TextView = layoutBannerContent.findViewById(R.id.bannerBody)
    private val bannerImage: ImageView = layoutBannerContent.findViewById(R.id.bannerImage)
    private val bannerButton2: MerakiButton = layoutBannerContent.findViewById(R.id.bannerButton2)
    private val bannerButton1: MerakiButton = layoutBannerContent.findViewById(R.id.bannerButton1)

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
        bannerTitle.text = HtmlCompat.fromHtml(item.title?:"", HtmlCompat.FROM_HTML_MODE_COMPACT)
        bannerBody.text = HtmlCompat.fromHtml(item.value?:"", HtmlCompat.FROM_HTML_MODE_COMPACT)

        item.image?.let {
            bannerImage.visibility = View.VISIBLE
            bannerImage.setImageResource(ResourceResolver(true).getDrawableId(bannerImage.context, it))
        }?:run{
            bannerImage.visibility = View.GONE
        }

    }

    private fun setActionButtons(
        actions: List<BannerAction>,
        urlCallback: (BannerAction?) -> Unit
    ) {
        actions.forEachIndexed { index, element ->
            if (index == 0) {
                bannerButton2.visibility = View.GONE
                bannerButton1.visibility = View.VISIBLE
                bannerButton1.setData(element, urlCallback)
            } else {
                bannerButton2.visibility = View.VISIBLE
                bannerButton2.setData(element, urlCallback)
            }
        }
    }

}

