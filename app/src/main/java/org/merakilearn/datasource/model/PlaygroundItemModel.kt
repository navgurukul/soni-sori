package org.merakilearn.datasource.model

import androidx.annotation.DrawableRes
import java.io.File

data class PlaygroundItemModel(
    val type: PlaygroundTypes,
    val name: String = "",
    val file: File = File(" "),
    @DrawableRes val iconResource: Int,
)

enum class PlaygroundTypes {
    PYTHON, TYPING_APP, PYTHON_FILE, SCRATCH, SCRATCH_FILE
}