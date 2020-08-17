package org.navgurukul.learn.ui.learn

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.ViewModelProviders
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.network.NetworkDataProvider
import org.navgurukul.learn.courses.network.retrofit.RetrofitClient
import org.navgurukul.learn.courses.network.retrofit.RetrofitDataProvider
import org.navgurukul.learn.courses.repository.CoursesRepositoryImpl

class LearnFragment : Fragment() {

    private lateinit var coursesDatabase: CoursesDatabase
    private lateinit var networkDataProvider: NetworkDataProvider
    private lateinit var learnViewModel: LearnViewModel


    private val viewModel: LearnViewModel by viewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_learn, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewModel.coursesData.observe(viewLifecycleOwner, Observer {
           if(it.isNotEmpty())
               mCourseAdapter.submitList(it)
        })
    }

    private fun initRecyclerView() {
        mCourseAdapter = CourseAdapter {
            toast("----"+it.first+"----")
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourse.layoutManager = layoutManager
        mBinding.recyclerviewCourse.adapter = mCourseAdapter
    }
}