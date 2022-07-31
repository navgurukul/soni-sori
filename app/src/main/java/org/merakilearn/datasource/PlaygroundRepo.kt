package org.merakilearn.datasource

import org.merakilearn.R
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes

class PlaygroundRepo {

    fun getAllPlaygrounds(): List<PlaygroundItemModel> {
        return arrayListOf(
            PlaygroundItemModel(
                PlaygroundTypes.PYTHON,
                R.string.python,
                R.drawable.ic_python_icon,
                R.color.python_item_bg_color
            ),
            PlaygroundItemModel(
                PlaygroundTypes.TYPING_APP,
                R.string.typing,
                R.drawable.ic_typing_icon,
                R.color.typing_item_bg_color
            ),
            PlaygroundItemModel(
                PlaygroundTypes.SCRATCH,
                R.string.scratch,
                R.drawable.ic_typing_icon,
                R.color.typing_item_bg_color
            )
        )
    }
}