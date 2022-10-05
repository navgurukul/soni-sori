package org.merakilearn.datasource

import org.merakilearn.R
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes

class PlaygroundRepo {

    fun getAllPlaygrounds(): List<PlaygroundItemModel> {
        return arrayListOf(
            PlaygroundItemModel(
                PlaygroundTypes.PYTHON,
                name="Python",
                iconResource = R.drawable.python_logo,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.TYPING_APP,
                name="Typing",
                iconResource = R.drawable.ic_icon_typing,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.SCRATCH,
                name="Scratch",
                iconResource = R.drawable.ic_scratch_cat,
            ),

        )
    }
}