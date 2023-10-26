package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.learn.expandablerecyclerviewlist.listener.ExpandCollapseListener
import org.navgurukul.learn.ui.learn.model.CategoryList
import org.navgurukul.learn.R
import org.navgurukul.learn.adapter.CategoryAdapter
import org.navgurukul.learn.databinding.FragmentC4caBinding
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.ui.learn.adapter.DotItemDecoration
import org.navgurukul.learn.ui.learn.model.Category

class C4CAFragment : Fragment()  {

    private val adapter = CategoryAdapter()
    private lateinit var mBinding: FragmentC4caBinding
    private val viewModel: LearnFragmentViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_c4ca, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.viewState.observe(viewLifecycleOwner) {
//
//        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is LearnFragmentViewEvents.OpenC4CAHomeFragment -> {
                    Log.d("C4CAFragment", "OpenC4CAHomeFragment ${it}")
                }
            }
        }

//        mCourseAdapter = CourseAdapter(requireContext()) {
//            viewModel.selectCourse(it)
//        }


        val data = listOf(
            Category("Module 1: Build Perspective on Climate Change", listOf(
                CategoryList("How earth has changed"),
                CategoryList("Intro to climate superheroes"),
                CategoryList("Why they become superheroes")
            )),
            Category("Module 2: Researcher - Solutions for Climate Action", listOf(
                CategoryList("Understanding vulnerability to climate impact"),
                CategoryList("Intro to events")
            )),
            Category("Module 3: Innovater - Building our Solutions for Climate Action", listOf(
                CategoryList("Loops and conditional loops introduction"),
                CategoryList("Loops and conditional loops introduction")
            ))
        )

        mBinding.categoryListRvFragmentC4ca.setHasFixedSize(true)
        mBinding.categoryListRvFragmentC4ca.layoutManager = LinearLayoutManager(activity)
        adapter.setExpandCollapseListener(object : ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
            }

            override fun onListItemCollapsed(position: Int) {

            }

        })
        mBinding.categoryListRvFragmentC4ca.adapter = adapter
        adapter.setExpandableParentItemList(data)
    }
}