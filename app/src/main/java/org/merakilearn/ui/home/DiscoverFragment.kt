package org.merakilearn.ui.home

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
import org.merakilearn.EnrollActivity
import org.merakilearn.MainActivity
import org.merakilearn.R
import org.merakilearn.databinding.FragmentDiscoverClassBinding
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.ui.home.adapter.DiscoverClassParentAdapter
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.ui.common.toast


class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
        const val TAG = "DiscoverFragment"
    }

    private lateinit var mBinding: FragmentDiscoverClassBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var discoverClassParentAdapter: DiscoverClassParentAdapter
    private var searchView: SearchView? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = (activity as MainActivity).toggleSearch(View.VISIBLE)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchDataForRV(null)
        initChipGroup()
        initDiscoverClassRV()
        initSearchListener()
    }


    private fun initSearchListener() {
        searchView?.setOnQueryTextListener(object :
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

        searchView?.setOnCloseListener {
            discoverClassParentAdapter.filter.filter("")
            false
        }
    }

    private fun initChipGroup() {
        val languageList = AppUtils.getAvailableLanguages(requireContext())
        for (index in languageList) {
            val chip = Chip(mBinding.languageChipGroup.context)
            chip.text = index.label
            chip.tag = index.code
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

        var lastCheckedId = View.NO_ID
        mBinding.languageChipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == View.NO_ID) {
                fetchDataForRV(null)
                return@setOnCheckedChangeListener
            }
            lastCheckedId = checkedId
            val chip: Chip = mBinding.languageChipGroup.findViewById(lastCheckedId)
            fetchDataForRV(chip.tag.toString())
        }

    }


    private fun fetchDataForRV(langCode: String?) {
        if (this::discoverClassParentAdapter.isInitialized)
            discoverClassParentAdapter.submitData(mutableListOf())
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.fetchUpcomingClass(langCode).observe(viewLifecycleOwner, Observer {
            toggleProgressBarVisibility(View.GONE)
            if (null != it && it.isNotEmpty()) {
                discoverClassParentAdapter.submitData(it as MutableList<Classes?>)
            } else {
                toast(getString(R.string.no_class_scheduled))
            }
        })
    }


    private fun initDiscoverClassRV() {
        discoverClassParentAdapter = DiscoverClassParentAdapter {
            EnrollActivity.start(requireContext(), it.id, false)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerview.layoutManager = layoutManager
        mBinding.recyclerview.adapter = discoverClassParentAdapter
    }


    private fun toggleProgressBarVisibility(visibility: Int) {
        mBinding.progressBarButton.visibility = visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).toggleSearch(View.GONE)
    }
}