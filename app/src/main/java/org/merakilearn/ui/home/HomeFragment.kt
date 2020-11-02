package org.merakilearn.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.EnrollActivity
import org.merakilearn.R
import org.merakilearn.databinding.FragmentHomeBinding
import org.merakilearn.ui.discover.DiscoverActivity
import org.merakilearn.ui.home.adapter.MyUpcomingClassAdapter
import org.merakilearn.ui.home.adapter.OtherCourseAdapter
import org.merakilearn.ui.home.adapter.WhereYouLeftAdapter
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.CourseDetailActivity


class HomeFragment : Fragment() {

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
        initMyClassViewAndData()
        (activity as? ToolbarConfigurable)?.setTitle(getString(R.string.app_name), R.attr.colorPrimary)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDiscoverClassButton()
        initOtherCourseViewAndData()
        initUpComingClassesRV()
    }

    private fun initOtherCourseViewAndData() {
        initOtherCourseRV()
        fetchDataOtherCourse()
    }

    private fun initMyClassViewAndData() {
        fetchMyClassData()
    }

    private fun initDiscoverClassButton() {
        mBinding.emptyMyClass.setOnClickListener {
            startDiscoverActivity()
        }

        mBinding.viewAll.setOnClickListener {
            startDiscoverActivity()
        }
    }

    private fun startDiscoverActivity() {
        DiscoverActivity.start(requireContext())
    }

    private fun fetchDataOtherCourse() {
        mBinding.progressBarButton.visibility = View.VISIBLE
        viewModel.fetchOtherCourseData().observe(viewLifecycleOwner, Observer {
            mBinding.progressBarButton.visibility = View.GONE
            if (null != it && it.isNotEmpty()) {
                mOtherCourseAdapter.submitList(it)
            }
        })
    }

    private fun fetchMyClassData() {
        mBinding.progressBarButtonMy.visibility = View.VISIBLE
        viewModel.fetchMyClasses().observe(viewLifecycleOwner, Observer {
            mBinding.progressBarButtonMy.visibility = View.GONE
            if (null != it && it.isNotEmpty()) {
                mBinding.emptyMyClass.visibility = View.GONE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.VISIBLE
                mMyUpcomingClassAdapter.submitList(it)
            } else {
                mBinding.emptyMyClass.visibility = View.VISIBLE
                mBinding.recyclerviewMyUpcomingClass.visibility = View.GONE
            }
        })
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
            CourseDetailActivity.start(requireContext(), it.id, it.name)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewOtherCourse.layoutManager = layoutManager
        mBinding.recyclerviewOtherCourse.adapter = mOtherCourseAdapter
        mBinding.recyclerviewOtherCourse.addItemDecoration(SpaceItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.spacing_3x), 0))
    }

    private fun initUpComingClassesRV() {
        mMyUpcomingClassAdapter = MyUpcomingClassAdapter {
            EnrollActivity.start(requireContext(), it.classes.id, true)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewMyUpcomingClass.layoutManager = layoutManager
        mBinding.recyclerviewMyUpcomingClass.addItemDecoration(SpaceItemDecoration(0, requireContext().resources.getDimensionPixelSize(R.dimen.spacing_2x)))
        mBinding.recyclerviewMyUpcomingClass.adapter = mMyUpcomingClassAdapter
    }

}