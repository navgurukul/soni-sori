package org.navgurukul.learn.ui.learn.c4ca

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.learn.R
import org.navgurukul.learn.adapter.CategoryAdapter
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.databinding.FragmentC4caBinding
import org.navgurukul.learn.expandablerecyclerviewlist.listener.ExpandCollapseListener
import org.navgurukul.learn.ui.common.toast

class C4CAFragment : Fragment() {

    private lateinit var expandableAdapter: CategoryAdapter
    private lateinit var mBinding: FragmentC4caBinding
    private val viewModel: C4CAFragmentViewModel by sharedViewModel()
    private val modules = mutableListOf<Module>()

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


        viewModel.viewState.observe(viewLifecycleOwner) {
            initToolBar(
                subtitle = it.subtitle,
                pathwayIcon = it.logo
            )
            initExpandableRecyclerView(it.moduleList)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is C4CAFragmentViewEvents.ShowToast -> toast(it.toastText)
                is C4CAFragmentViewEvents.OpenModuleCourseDetailActivity -> {
                    ModuleCourseContentActivity.start(
                        requireContext(),
                        it.courseId
                    )
                    Log.d("C4CAFragment", "OpenModuleCourseDetailActivity Successfuly ${it.courseName}")
                }
            }
        }

        initToolBar()
    }

    private fun initToolBar(
        subtitle: String? = null,
        pathwayIcon: String? = null
    ) {
        if (subtitle != null) {
            (activity as? ToolbarConfigurable)?.configure(
                subtitle,
                R.attr.textPrimary,
                false,
                null,
                null,
                null, null,
                showPathwayIcon = true,
                pathwayIcon = pathwayIcon
            )
        }

    }

    private fun initExpandableRecyclerView(modules: List<Module>) {
        expandableAdapter = CategoryAdapter(requireContext()) {
            viewModel.selectCourse(it)
        }
        mBinding.module.setHasFixedSize(true)
        mBinding.module.layoutManager = LinearLayoutManager(activity)
        expandableAdapter.setExpandCollapseListener(object : ExpandCollapseListener {
            override fun onListItemExpanded(position: Int) {
                Toast.makeText(
                    activity,
                    "Expanded: $position",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onListItemCollapsed(position: Int) {
                Toast.makeText(
                    activity,
                    "Collapsed: $position",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        mBinding.module.adapter = expandableAdapter
        expandableAdapter.setExpandableParentItemList(modules)
    }
}