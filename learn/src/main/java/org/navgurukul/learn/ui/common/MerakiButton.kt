package org.navgurukul.learn.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
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
    }

    fun setVariant(variant: MerakiButtonType){
        if(variant == MerakiButtonType.secondary){
            merakiButton.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_right, null)
            merakiButton.iconGravity = ICON_GRAVITY_END
            merakiButton.setIconTintResource(R.color.primaryColor)
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }else{
            merakiButton.icon = null
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }
    }
}