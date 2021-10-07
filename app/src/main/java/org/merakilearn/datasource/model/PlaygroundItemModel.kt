package org.merakilearn.datasource.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PlaygroundItemModel(
    val type: PlaygroundTypes,
    @StringRes val name: Int,
    @DrawableRes val iconResource: Int,
    @ColorRes val backgroundColor: Int,
)

enum class PlaygroundTypes {
    PYTHON, TYPING_APP
}