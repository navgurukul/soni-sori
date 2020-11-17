package org.navgurukul.chat.features.reactions

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import org.navgurukul.chat.EmojiCompatFontProvider
import org.navgurukul.chat.R
import org.navgurukul.chat.core.ui.list.genericFooterItem
import org.navgurukul.commonui.resources.StringProvider

class EmojiSearchResultController(
    private val stringProvider: StringProvider,
    private val fontProvider: EmojiCompatFontProvider
) : TypedEpoxyController<EmojiSearchResultViewState>() {

    var emojiTypeface: Typeface? = fontProvider.typeface

    private val fontProviderListener = object : EmojiCompatFontProvider.FontProviderListener {
        override fun compatibilityFontUpdate(typeface: Typeface?) {
            emojiTypeface = typeface
        }
    }

    init {
        fontProvider.addListener(fontProviderListener)
    }

    var listener: ReactionClickListener? = null

    override fun buildModels(data: EmojiSearchResultViewState?) {
        val results = data?.results ?: return

        if (results.isEmpty()) {
            if (data.query.isEmpty()) {
                // display 'Type something to find'
                genericFooterItem {
                    id("type.query.item")
                    text(stringProvider.getString(R.string.reaction_search_type_hint))
                }
            } else {
                // Display no search Results
                genericFooterItem {
                    id("no.results.item")
                    text(stringProvider.getString(R.string.no_result_placeholder))
                }
            }
        } else {
            // Build the search results
            results.forEach {
                emojiSearchResultItem {
                    id(it.name)
                    emojiItem(it)
                    emojiTypeFace(emojiTypeface)
                    currentQuery(data.query)
                    onClickListener(listener)
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        fontProvider.removeListener(fontProviderListener)
    }
}
