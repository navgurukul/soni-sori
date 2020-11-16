package org.navgurukul.chat.core.epoxy.bottomsheet

import android.graphics.Typeface
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.EmojiCompatFontProvider
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel

/**
 * A quick reaction list for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetQuickReactionsItem : MerakiEpoxyModel<BottomSheetQuickReactionsItem.Holder>() {

    @EpoxyAttribute
    lateinit var fontProvider: EmojiCompatFontProvider
    @EpoxyAttribute
    lateinit var texts: List<String>
    @EpoxyAttribute
    lateinit var selecteds: List<Boolean>
    @EpoxyAttribute
    var listener: Listener? = null

    override fun getDefaultLayout(): Int = R.layout.item_bottom_sheet_quick_reaction

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.textViews.forEachIndexed { index, textView ->
            textView.typeface = fontProvider.typeface ?: Typeface.DEFAULT
            textView.text = texts[index]
            textView.alpha = if (selecteds[index]) 0.2f else 1f

            textView.setOnClickListener {
                listener?.didSelect(texts[index], !selecteds[index])
            }
        }
    }

    class Holder : MerakiEpoxyHolder() {
        private val quickReaction0 by bind<TextView>(R.id.quickReaction0)
        private val quickReaction1 by bind<TextView>(R.id.quickReaction1)
        private val quickReaction2 by bind<TextView>(R.id.quickReaction2)
        private val quickReaction3 by bind<TextView>(R.id.quickReaction3)
        private val quickReaction4 by bind<TextView>(R.id.quickReaction4)
        private val quickReaction5 by bind<TextView>(R.id.quickReaction5)
        private val quickReaction6 by bind<TextView>(R.id.quickReaction6)
        private val quickReaction7 by bind<TextView>(R.id.quickReaction7)

        val textViews
            get() = listOf(
                    quickReaction0,
                    quickReaction1,
                    quickReaction2,
                    quickReaction3,
                    quickReaction4,
                    quickReaction5,
                    quickReaction6,
                    quickReaction7
            )
    }

    interface Listener {
        fun didSelect(emoji: String, selected: Boolean)
    }
}
