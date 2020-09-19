package org.navgurukul.saral.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.saral.EnrollActivity
import org.navgurukul.saral.R
import org.navgurukul.saral.databinding.FragmentHomeBinding
import org.navgurukul.saral.ui.home.adapter.MyUpcomingClassAdapter
import org.navgurukul.saral.ui.home.adapter.OtherCourseAdapter
import org.navgurukul.saral.ui.home.adapter.WhereYouLeftAdapter
import org.navgurukul.saral.ui.onboarding.LoginFragment
import org.navgurukul.saral.util.AppUtils


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        const val TAG = "HomeFragment"
    }

    private lateinit var mBinding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var mWhereYouLeftAdapter: WhereYouLeftAdapter
    private lateinit var mOtherCourseAdapter: OtherCourseAdapter
    private lateinit var mMyUpcomingClassAdapter: MyUpcomingClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        fetchDataForRV()
        initRecyclerView()
        initDiscoverClassButton()
    }

    private fun initDiscoverClassButton() {
        mBinding.emptyMyClass.root.setOnClickListener {
            startDiscoverFragment()
        }

        mBinding.viewAll.setOnClickListener {
            startDiscoverFragment()
        }
    }

    private fun startDiscoverFragment() {
        AppUtils.changeFragment(
            parentFragmentManager,
            DiscoverFragment.newInstance(),
            R.id.nav_host_fragment,
            true,
            LoginFragment.TAG
        )
    }

    private fun fetchDataForRV() {
        viewModel.fetchWhereYouLeftData().observe(viewLifecycleOwner, Observer {
            if (null != it && it.isNotEmpty()) {
                mWhereYouLeftAdapter.submitList(it)
            }
        })

        viewModel.fetchMyClasses().observe(viewLifecycleOwner, Observer {
            toggleProgressBarVisibility(View.VISIBLE)
            if (null != it && it.isNotEmpty()) {
                toggleProgressBarVisibility(View.GONE)
                mBinding.emptyMyClass.root.visibility = View.GONE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.VISIBLE
                mMyUpcomingClassAdapter.submitList(it)
            } else {
                toggleProgressBarVisibility(View.GONE)
                mBinding.emptyMyClass.root.visibility = View.VISIBLE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.GONE
            }
        })

        viewModel.fetchOtherCourseData().observe(viewLifecycleOwner, Observer {
            if (null != it && it.isNotEmpty()) {
                mOtherCourseAdapter.submitList(it)
            }
        })
    }

    private fun initRecyclerView() {
        initWhereYouLeftRV()
        initUpComingClassesRV()
        initOtherCourseRV()
    }


    private fun initWhereYouLeftRV() {
        mWhereYouLeftAdapter = WhereYouLeftAdapter {
            toast(it.first.toString())
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourseContinue.layoutManager = layoutManager
        mBinding.recyclerviewCourseContinue.adapter = mWhereYouLeftAdapter
    }

    private fun initOtherCourseRV() {
        mOtherCourseAdapter = OtherCourseAdapter {
            toast(it.first.toString())
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewOtherCourse.layoutManager = layoutManager
        mBinding.recyclerviewOtherCourse.adapter = mOtherCourseAdapter
    }

    private fun initUpComingClassesRV() {
        mMyUpcomingClassAdapter = MyUpcomingClassAdapter {
            EnrollActivity.start(requireContext(),it.classes.first(),true)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewMyUpcomingClass.layoutManager = layoutManager
        mBinding.recyclerviewMyUpcomingClass.adapter = mMyUpcomingClassAdapter
    }


    private fun toggleProgressBarVisibility(visibility: Int) {
        mBinding.progressBarButton.visibility = visibility
    }

}