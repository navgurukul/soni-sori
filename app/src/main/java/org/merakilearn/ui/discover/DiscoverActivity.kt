package org.merakilearn.ui.discover

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_discover_class.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.EnrollActivity
import org.merakilearn.R
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

    var states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    )

    var colors = intArrayOf(
        Color.RED,
        Color.WHITE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_discover_class)

        setupToolbar()
        initChipGroup()
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
            searchView.isEnabled = it.searchEnabled
            if (it.showError) {
                emptyStateView.isVisible = true
                emptyStateView.state = EmptyStateView.State.NO_CONTENT
            } else {
                emptyStateView.isVisible = false
            }
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

    private fun initChipGroup() {
        val lang = resources.getStringArray(R.array.language)
        for (index in lang) {
            val chip = Chip(languageChipGroup.context)
            chip.text = index
            chip.setHintTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhite
                    )
                )
            )
            chip.chipBackgroundColor = ColorStateList(states, colors)
            chip.chipStrokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorRed))
            chip.chipStrokeWidth = 2.0f
            chip.isClickable = true
            chip.isCheckable = true
            languageChipGroup.addView(chip)
        }
    }
}