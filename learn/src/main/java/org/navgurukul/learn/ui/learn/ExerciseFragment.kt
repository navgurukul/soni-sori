package org.navgurukul.learn.ui.learn


import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.FragmentExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

@Parcelize
data class ExerciseFragmentArgs(
    val isFirst: Boolean,
    val isLast: Boolean,
    var isCompleted: Boolean,
    val courseId: String,
    val exerciseId: String
) : Parcelable

class ExerciseFragment : Fragment() {

    private val args: ExerciseFragmentArgs by fragmentArgs()
    private val fragmentViewModel: ExerciseFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var mBinding: FragmentExerciseBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private val merakiNavigator: MerakiNavigator by inject()

    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            exerciseId: String
        ): ExerciseFragment {
            return ExerciseFragment().apply {
                arguments = ExerciseFragmentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    exerciseId
                ).toBundle()
            }
        }

        const val TAG = "ExerciseFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ExerciseFragmentViewModel.ExerciseFragmentViewEvents.ShowToast -> toast(it.toastText)
            }
        })

        fragmentViewModel.viewState.observe(viewLifecycleOwner, {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            showErrorScreen(it.isError)

            if (!it.isError)
                contentAdapter.submitList(it.exerciseList)
        })

        initContentRV()
        initScreenRefresh()

    }

    private fun showErrorScreen(isError: Boolean) {
        if (isError) {
            mBinding.errorLayout.root.visibility = View.VISIBLE
            mBinding.contentLayout.visibility = View.GONE
        } else {
            mBinding.errorLayout.root.visibility = View.GONE
            mBinding.contentLayout.visibility = View.VISIBLE
        }
    }

    private fun initScreenRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.RequestContentRefresh)
            mBinding.swipeContainer.isRefreshing = false
        }

        mBinding.errorLayout.btnRefresh.setOnClickListener {
            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.RequestContentRefresh)
        }
    }

    private fun initContentRV() {
        contentAdapter = ExerciseContentAdapter(this.requireContext(), {
            if (it is CodeBaseCourseContent) {
                if (!it.value.isNullOrBlank()) {
                    val fromHtml = it.value.replace("<br>", "\n").replace("&emsp;", " ")
                    merakiNavigator.openPlayground(this.requireContext(), fromHtml)
                }
            } else if (it is LinkBaseCourseContent) {
                it.link?.let { url ->
                    merakiNavigator.openCustomTab(url, this.requireContext())
                }
            }
        }) {
            it?.let { action ->
                action.url?.let { url ->
                    merakiNavigator.openDeepLink(this.requireActivity(), url, action.data)
                }
            }
        }

        val layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerViewSlug.layoutManager = layoutManager
        mBinding.recyclerViewSlug.adapter = contentAdapter
        mBinding.recyclerViewSlug.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_4x), 0)
        )

    }

}