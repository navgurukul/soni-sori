package org.navgurukul.learn.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import org.navgurukul.commonui.resources.ResourceResolver
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BannerAction
import org.navgurukul.learn.courses.db.models.MerakiButtonType

class MerakiButton @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defaultStyleAttr: Int = 0,
) : MaterialButton(context, attributes, defaultStyleAttr) {

    private var merakiButton: MerakiButton

    init {
        inflate(context, R.layout.layout_meraki_button, null)
        merakiButton = findViewById(R.id.merakiButton)
    }

    fun setData(element:BannerAction, action: (BannerAction) -> Unit){
        merakiButton.text = element.label?:""

        merakiButton.setOnClickListener {
            action.invoke(element)
        }

        element.icon?.let {
            merakiButton.icon = ResourcesCompat.getDrawable(
                resources,
                ResourceResolver.getDrawableId(merakiButton.context, it),
                null
            )

            merakiButton.iconGravity = ICON_GRAVITY_END
        }?.run { merakiButton.icon = null }

        element.variant?.let {
            setVariant(it)
        }
    }

    fun setVariant(variant: MerakiButtonType){
        if(variant == MerakiButtonType.secondary){
            merakiButton.backgroundTintList = null
            merakiButton.setIconTintResource(R.color.primaryColor)
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }else{
            merakiButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        }
    }
}