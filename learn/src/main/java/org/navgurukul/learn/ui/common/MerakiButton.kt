package org.navgurukul.learn.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import org.navgurukul.learn.R

class MerakiButton @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defaultStyleAttr: Int = 0,
) : MaterialButton(context, attributes, defaultStyleAttr) {

    private var merakiButton: MaterialButton

    var variant: MerakiButtonType? = null

    set(value){
        field = value
        setVariantDesign()
    }
    get() = field

    init {
        merakiButton = this
        merakiButton.setTextAppearance(context, R.style.buttonTextAppearanceStyle)
    }

    fun setVariantDesign(){
        if(variant == MerakiButtonType.secondary){
            merakiButton.background = null
            merakiButton.setIconTintResource(R.color.primaryColor)
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
            merakiButton.cornerRadius = 0
        }else{
            merakiButton.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
            merakiButton.setIconTintResource(R.color.colorWhite)
            merakiButton.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            merakiButton.cornerRadius = 8
        }
    }
}

enum class MerakiButtonType {
    primary, secondary
}