package org.navgurukul.saral.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.saral.EnrollActivity
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.FragmentDiscoverClassBinding
import org.navgurukul.saral.ui.home.adapter.DiscoverClassParentAdapter


class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
        const val TAG = "DiscoverFragment"
    }

    private lateinit var mBinding: FragmentDiscoverClassBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var discoverClassParentAdapter: DiscoverClassParentAdapter
    var states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    )

    var colors = intArrayOf(
        Color.RED,
        Color.WHITE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_discover_class, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initChipGroup()
        fetchDataForRV()
        initDiscoverClassRV()
        initSearchListener()

    }

    private fun initSearchListener() {
        mBinding.idHeader.ivBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        mBinding.idHeader.searchView.setOnSearchClickListener {
            mBinding.idHeader.tvSearchTitle.visibility = View.GONE
        }
        mBinding.idHeader.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                discoverClassParentAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                discoverClassParentAdapter.filter.filter(newText)
                return false
            }
        })

        mBinding.idHeader.searchView.setOnCloseListener {
            discoverClassParentAdapter.filter.filter("")
            mBinding.idHeader.tvSearchTitle.visibility = View.VISIBLE
            false
        }
    }

    private fun initChipGroup() {
        val lang = resources.getStringArray(R.array.language)
        for (index in lang) {
            val chip = Chip(mBinding.languageChipGroup.context)
            chip.text = index
            chip.setHintTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorWhite
                    )
                )
            )
            chip.chipBackgroundColor = ColorStateList(states, colors)
            chip.chipStrokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorRed))
            chip.chipStrokeWidth = 2.0f
            chip.isClickable = true
            chip.isCheckable = true
            mBinding.languageChipGroup.addView(chip)
        }
    }


    private fun fetchDataForRV() {
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.fetchUpcomingClass().observe(viewLifecycleOwner, Observer {
            toggleProgressBarVisibility(View.GONE)
            if (null != it && it.isNotEmpty()) {
                discoverClassParentAdapter.submitData(it)
            } else {
                toast(getString(R.string.no_class_scheduled))
            }
        })
    }


    private fun initDiscoverClassRV() {
        discoverClassParentAdapter = DiscoverClassParentAdapter {
            EnrollActivity.start(requireContext(), it, false)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerview.layoutManager = layoutManager
        mBinding.recyclerview.adapter = discoverClassParentAdapter
    }


    private fun toggleProgressBarVisibility(visibility: Int) {
        mBinding.progressBarButton.visibility = visibility
    }

}