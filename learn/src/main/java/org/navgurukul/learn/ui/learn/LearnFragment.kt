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
import org.navgurukul.learn.R
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
        viewModel.fetchCourseData().observe(viewLifecycleOwner, Observer {
            if (null != it && it.isNotEmpty())
                mCourseAdapter.submitList(it)
        })
    }

    private fun initRecyclerView() {
        mCourseAdapter = CourseAdapter {
            CourseDetailActivity.start(requireContext(), it.first.id,
                it.first.name)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourse.layoutManager = layoutManager
        mBinding.recyclerviewCourse.adapter = mCourseAdapter
    }
}