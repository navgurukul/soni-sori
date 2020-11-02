package org.merakilearn.ui.discover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_discover_class.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.EnrollActivity
import org.merakilearn.R
import org.merakilearn.core.extentions.enableChildren
import org.merakilearn.datasource.network.model.Language
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.views.EmptyStateView


class DiscoverActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, DiscoverActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: DiscoverViewModel by viewModel()

    private val discoverClassParentAdapter: DiscoverClassParentAdapter by lazy {
        DiscoverClassParentAdapter {
            EnrollActivity.start(this, it.id, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_discover_class)

        setupToolbar()
        setupObserver()
        initSearchListener()

        recyclerview.adapter = discoverClassParentAdapter
        recyclerview.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimensionPixelSize(R.dimen.spacing_8x),
                0
            )
        )
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun setupObserver() {
        viewModel.viewState.observe(this, Observer {
            progress_bar_button.isVisible = it.isLoading
            discoverClassParentAdapter.submitList(it.itemList)
            searchView.enableChildren(it.searchEnabled)
            if (it.showError || it.showNoContent) {
                emptyStateView.isVisible = true
                emptyStateView.state =
                    if (it.showNoContent) EmptyStateView.State.NO_CONTENT else EmptyStateView.State.ERROR
            } else {
                emptyStateView.isVisible = false
            }
        })

        viewModel.supportedLanguages.observe(this, Observer {
            setupChipGroup(it)
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(app_bar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun initSearchListener() {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handle(DiscoverViewActions.Query(query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handle(DiscoverViewActions.Query(newText))

                iv_search_icon.isVisible = newText.isNullOrEmpty()
                return false
            }
        })
    }

    private fun setupChipGroup(languageList: List<Language>) {
        if (languageChipGroup.childCount > 0) {
            languageChipGroup.removeAllViews()
        }

        if (languageList.isEmpty()) {
            languageChipGroup.isVisible = false
        } else {
            languageChipGroup.isVisible = true
            for (index in languageList) {
                val chip = Chip(this)
                chip.text = index.label
                chip.tag = index.code
                chip.setTextColor(AppCompatResources.getColorStateList(this, R.color.language_chip_text_color))
                chip.chipBackgroundColor = AppCompatResources.getColorStateList(this, R.color.language_chip_background_color)
                chip.chipStrokeColor = AppCompatResources.getColorStateList(this, R.color.language_chip_border_color)
                chip.setChipStrokeWidthResource(R.dimen.chip_border_width)
                chip.isClickable = true
                chip.isCheckable = true
                chip.checkedIcon = null
                languageChipGroup.addView(chip)
            }
            languageChipGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == View.NO_ID) {
                    viewModel.handle(DiscoverViewActions.FilterFromClass(null))
                    return@setOnCheckedChangeListener
                }
                val chip: Chip = languageChipGroup.findViewById(checkedId)
                viewModel.handle(DiscoverViewActions.FilterFromClass((chip.tag.toString())))
            }
        }
    }
}