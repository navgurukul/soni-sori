package org.navgurukul.saral.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.navgurukul.saral.datasource.network.model.ClassesContainer
import org.navgurukul.saral.ui.home.adapter.DiscoverClassParentAdapter


class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
        const val TAG = "DiscoverFragment"
    }

    private lateinit var mBinding: FragmentDiscoverClassBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var discoverClassParentAdapter: DiscoverClassParentAdapter


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

    }

    private fun initChipGroup() {
        val lang = resources.getStringArray(R.array.language)
        for (index in lang) {
            val chip = Chip(mBinding.languageChipGroup.context)
            chip.text = index
            chip.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            chip.isClickable = true
            chip.isCheckable = true
            mBinding.languageChipGroup.addView(chip)
        }
    }


    private fun fetchDataForRV() {
        viewModel.fetchUpcomingClass().observe(viewLifecycleOwner, Observer {
            toggleProgressBarVisibility(View.VISIBLE)
            if (null != it && it.isNotEmpty()) {
                toggleProgressBarVisibility(View.GONE)
                val groupedData = parseData(it)
                discoverClassParentAdapter.submitList(groupedData)
            } else {
                toggleProgressBarVisibility(View.GONE)
                toast(getString(R.string.no_class_scheduled))
            }
        })
    }

    private fun parseData(it: MutableList<ClassesContainer.Classes?>): MutableList<DiscoverClassParentAdapter.DiscoverData>? {
        val data = it.groupBy {
            it?.startTime
        }
        val list = mutableListOf<DiscoverClassParentAdapter.DiscoverData>()
        data.forEach {
            list.add(
                DiscoverClassParentAdapter.DiscoverData(
                    it.key,
                    it.value as MutableList<ClassesContainer.Classes>
                )
            )
        }
        return list
    }

    private fun initDiscoverClassRV() {
        discoverClassParentAdapter = DiscoverClassParentAdapter {
            EnrollActivity.start(requireContext(), it, true)
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