package org.navgurukul.learn.ui.learn.c4ca

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.learn.R
import org.navgurukul.learn.adapter.CategoryAdapter
import org.navgurukul.learn.databinding.FragmentC4caBinding
import org.navgurukul.learn.expandablerecyclerviewlist.listener.ExpandCollapseListener
import org.navgurukul.learn.ui.learn.model.Category
import org.navgurukul.learn.ui.learn.model.CategoryList

class C4CAFragment : Fragment() {

    private var expandableAdapter = CategoryAdapter()
    private lateinit var mBinding: FragmentC4caBinding
    private val viewModel: C4CAFragmentViewModel by sharedViewModel()

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
        //initExpandableRecyclerView()

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is C4CAFragmentViewEvents.GetC4CAPathways -> {
                    Log.d(
                        "C4CAFragmentWithViewEvents",
                        "initExpandableRecyclerView: ${it.C4CA}"
                    )
                    it.C4CA
                    val ModuleAdapter = C4CAAdapter()
                    ModuleAdapter.submitList(it.C4CA)
                    mBinding.module.layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    mBinding.module.adapter = ModuleAdapter

                    // Expandable RecyclerView
//                    mBinding.categoryListRvFragmentC4ca.setHasFixedSize(true)
//                    mBinding.categoryListRvFragmentC4ca.layoutManager = LinearLayoutManager(activity)
//                    expandableAdapter.setExpandCollapseListener(object : ExpandCollapseListener {
//                        override fun onListItemExpanded(position: Int) {
//                        }
//
//                        override fun onListItemCollapsed(position: Int) {
//
//                        }
//
//                    })
//
//                    mBinding.categoryListRvFragmentC4ca.adapter = expandableAdapter
//                    expandableAdapter.setExpandableParentItemList(it.C4CA.map { Category(it.name, it.courses!!) })
                }
            }
        }
        initToolBar()
        viewModel.viewState.observe(viewLifecycleOwner) {
            Log.d("C4CAFragment", "onViewCreated: $it")

        }
    }

    private fun initToolBar() {
        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.c4ca_title),
            R.attr.textPrimary,
            false,
            null,
            null,
            null, null,
            showPathwayIcon = true,
            pathwayIcon = "https://s3strapi-project.s3.ap-south-1.amazonaws.com/random_c5ad8b4ada.svg"
        )

    }

    private fun initExpandableRecyclerView() {
        val data = listOf(
            Category(
                "Module 1: Build Perspective on Climate Change", listOf(
                    CategoryList("How earth has changed"),
                    CategoryList("Intro to climate superheroes"),
                    CategoryList("Why they become superheroes")
                )
            ),
            Category(
                "Module 2: Researcher - Solutions for Climate Action", listOf(
                    CategoryList("Understanding vulnerability to climate impact"),
                    CategoryList("Intro to events")
                )
            ),
            Category(
                "Module 3: Innovater - Building our Solutions for Climate Action", listOf(
                    CategoryList("Loops and conditional loops introduction"),
                    CategoryList("Loops and conditional loops introduction")
                )
            )
        )
        mBinding.categoryListRvFragmentC4ca.setHasFixedSize(true)
        mBinding.categoryListRvFragmentC4ca.layoutManager = LinearLayoutManager(activity)
        expandableAdapter.setExpandCollapseListener(object : ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
            }

            override fun onListItemCollapsed(position: Int) {

            }

        })

        mBinding.categoryListRvFragmentC4ca.adapter = expandableAdapter
        expandableAdapter.setExpandableParentItemList(data)
    }
}