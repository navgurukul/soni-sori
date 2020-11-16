package org.navgurukul.chat.features.reactions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.widget.queryTextChanges
import org.navgurukul.chat.features.reactions.data.EmojiDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_emoji_reaction_picker.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.EmojiCompatFontProvider
import org.navgurukul.chat.R
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 *
 * TODO: Loading indicator while getting emoji data source?
 * TODO: Finish Refactor to vector base activity
 */
class EmojiReactionPickerActivity : ChatBaseActivity(),
        EmojiCompatFontProvider.FontProviderListener {

    val viewModel: EmojiChooserViewModel by viewModel()

    override fun getMenuRes() = R.menu.menu_emoji_reaction_picker

    private val emojiCompatFontProvider: EmojiCompatFontProvider by inject()
    private val emojiDataSource: EmojiDataSource by inject()

    private val searchResultViewModel: EmojiSearchResultViewModel by viewModel()

    private var tabLayoutSelectionListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.scrollToSection(tab.position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji_reaction_picker)

        initUiAndData()

        supportActionBar?.setTitle(R.string.title_activity_emoji_reaction_picker) ?: run {
            setTitle(R.string.title_activity_emoji_reaction_picker)
        }
    }

    private fun initUiAndData() {
        configureToolbar(emojiPickerToolbar)
        emojiCompatFontProvider.let {
            EmojiDrawView.configureTextPaint(this, it.typeface)
            it.addListener(this)
        }

        viewModel.eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        emojiDataSource.rawData.categories.forEach { category ->
            val s = category.emojis[0]
            tabs.newTab()
                    .also { tab ->
                        tab.text = emojiDataSource.rawData.emojis[s]!!.emoji
                        tab.contentDescription = category.name
                    }
                    .also { tab ->
                        tabs.addTab(tab)
                    }
        }
        tabs.addOnTabSelectedListener(tabLayoutSelectionListener)

        viewModel.currentSection.observe(this, Observer { section ->
            section?.let {
                tabs.removeOnTabSelectedListener(tabLayoutSelectionListener)
                tabs.getTabAt(it)?.select()
                tabs.addOnTabSelectedListener(tabLayoutSelectionListener)
            }
        })

        viewModel.navigateEvent.observe(this, Observer {
            if (it == EmojiChooserViewModel.NAVIGATE_FINISH) {
                // finish with result
                val dataResult = Intent()
                dataResult.putExtra(EXTRA_REACTION_RESULT, viewModel.selectedReaction)
                dataResult.putExtra(EXTRA_EVENT_ID, viewModel.eventId)
                setResult(Activity.RESULT_OK, dataResult)
                finish()
            }
        })

        emojiPickerWholeListFragmentContainer.isVisible = true
        emojiPickerFilteredListFragmentContainer.isVisible = false
        tabs.isVisible = true
    }

    override fun compatibilityFontUpdate(typeface: Typeface?) {
        EmojiDrawView.configureTextPaint(this, typeface)
    }

    override fun onDestroy() {
        emojiCompatFontProvider.removeListener(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(getMenuRes(), menu)

        val searchItem = menu.findItem(R.id.search)
        (searchItem.actionView as? SearchView)?.let { searchView ->
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    searchView.isIconified = false
                    searchView.requestFocusFromTouch()
                    // we want to force the tool bar as visible even if hidden with scroll flags
                    emojiPickerToolbar.minimumHeight = getActionBarSize()
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    // when back, clear all search
                    emojiPickerToolbar.minimumHeight = 0
                    searchView.setQuery("", true)
                    return true
                }
            })

            searchView.queryTextChanges()
                    .throttleWithTimeout(600, TimeUnit.MILLISECONDS)
                    .doOnError { err -> Timber.e(err) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { query ->
                        onQueryText(query.toString())
                    }
                    .disposeOnDestroy()
        }
        return true
    }

    // TODO move to ThemeUtils when core module is created
    private fun getActionBarSize(): Int {
        return try {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.actionBarSize, typedValue, true)
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
        } catch (e: Exception) {
            // Timber.e(e, "Unable to get color")
            TypedValue.complexToDimensionPixelSize(56, resources.displayMetrics)
        }
    }

    private fun onQueryText(query: String) {
        if (query.isEmpty()) {
            tabs.isVisible = true
            emojiPickerWholeListFragmentContainer.isVisible = true
            emojiPickerFilteredListFragmentContainer.isVisible = false
        } else {
            tabs.isVisible = false
            emojiPickerWholeListFragmentContainer.isVisible = false
            emojiPickerFilteredListFragmentContainer.isVisible = true
            searchResultViewModel.handle(EmojiSearchAction.UpdateQuery(query))
        }
    }

    companion object {

        private const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
        private const val EXTRA_REACTION_RESULT = "EXTRA_REACTION_RESULT"

        fun intent(context: Context, eventId: String): Intent {
            val intent = Intent(context, EmojiReactionPickerActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            return intent
        }

        fun getOutput(data: Intent): Pair<String, String>? {
            val eventId = data.getStringExtra(EXTRA_EVENT_ID) ?: return null
            val reaction = data.getStringExtra(EXTRA_REACTION_RESULT) ?: return null
            return eventId to reaction
        }
    }
}
