package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter


class LearnFragment : Fragment() {

    private val viewModel: LearnViewModel by viewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_learn, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        mBinding.progressBarButton.visibility = View.VISIBLE
        fetchData(false)

        initSwipeRefresh()

        (activity as? ToolbarConfigurable)?.setTitle(getString(R.string.courses), R.attr.textPrimary)
    }

    private fun fetchData(forceUpdate: Boolean) {
        viewModel.fetchCourseData(forceUpdate).observe(viewLifecycleOwner, Observer {
            if (null != it && it.isNotEmpty()) {
                mBinding.progressBarButton.visibility = View.GONE
                mCourseAdapter.submitList(it)
            }
            //TODO Please implement error or empty state here
        })
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fetchData(true)
            mBinding.swipeContainer.isRefreshing = false
        }
    }

    private fun initRecyclerView() {
        mCourseAdapter = CourseAdapter {
            startDesiredActivity(it.first)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourse.layoutManager = layoutManager
        mBinding.recyclerviewCourse.adapter = mCourseAdapter
        mBinding.recyclerviewCourse.addItemDecoration(SpaceItemDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.spacing_3x), 0))
    }

    private fun startDesiredActivity(it: Course) {
        viewModel.startDesiredActivity(it.id).observe(viewLifecycleOwner, Observer { itt ->
            if (itt.isNotEmpty())
                CourseSlugDetailActivity.start(requireContext(), itt.first())
            else
                CourseDetailActivity.start(requireContext(), it.id, it.name)
        })
    }

    companion object {
        private const val TAG = "LearnFragment"
    }
}