package org.navgurukul.learn.ui.learn.viewholder

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.ImageBaseCourseContent


class ImageCourseViewHolder(itemView: View, private val glideRequest: RequestManager) :
    BaseCourseViewHolder(itemView) {

    private val imageView: ImageView = populateStub(R.layout.item_image_content)

    val childLayoutParams = imageView.layoutParams

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
//        setViewHeight(1.5)
    }

    fun bindView(item: ImageBaseCourseContent) {
        super.bind(item)

        item.value?.let { url ->
            glideRequest.load(url).into(imageView);
        }
        imageView.contentDescription = item.alt

    }

    fun setViewHeight(widthHeightRatio: Double) {
        childLayoutParams.height = (Resources.getSystem().getDisplayMetrics().widthPixels/widthHeightRatio).toInt()
    }
}

