package org.navgurukul.learn.ui.learn

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    val isCompleted: Boolean,
    val courseId: String,
    val exerciseId: String
) : Parcelable

class ExerciseFragment : Fragment() {

    private val args: ExerciseFragmentArgs by fragmentArgs()
    private val fragmentViewModel: ExerciseFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var navigationClickListener: ExerciseNavigationClickListener
    private lateinit var mBinding: FragmentExerciseBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private val merakiNavigator: MerakiNavigator by inject()

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                showExerciseNavigationBar(this)
            }
        }
    }

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
            contentAdapter.submitList(it.exerciseList)
        })

//        mBinding.recyclerViewSlug.postDelayed( {
//                if(isRecyclerScrollable(mBinding.recyclerViewSlug))
//                    mBinding.recyclerViewSlug.addOnScrollListener(scrollListener)
//                else
//                    showExerciseNavigationBar()
//            }, 200)

        initContentRV()
        initSwipeRefresh()
    }

    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.PulledDownToRefresh)
            mBinding.swipeContainer.isRefreshing = false
        }
    }

    private fun initContentRV() {
        contentAdapter = ExerciseContentAdapter(this.requireContext(), {
            if (it is CodeBaseCourseContent) {
                if (!it.value.isNullOrBlank()) {
                    merakiNavigator.openPlayground(this.requireContext(), it.value)
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

    fun isRecyclerScrollable(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val adapter = recyclerView.adapter
        return if (adapter == null) false else layoutManager.findLastCompletelyVisibleItemPosition() < adapter.itemCount - 1
    }

//    private fun showExerciseNavigationBar(scrollListener: RecyclerView.OnScrollListener ?= null) {
//        mBinding.bottomNavigationExercise.visibility = View.VISIBLE
//        scrollListener?.let {
//            mBinding.recyclerViewSlug.removeOnScrollListener(it)
//        }
//        mBinding.bottomNavigationExercise.setView(isCompleted, isFirst, isLast)
//
//        mBinding.bottomNavigationExercise.setMarkAction {
//            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.MarkCompleteClicked(currentStudy))
//
//            navigationClickListener.onMarkCompleteClick()
//
//            isCompleted = true
//            mBinding.bottomNavigationExercise.setView(isCompleted, isFirst, isLast)
//        }
//
//        mBinding.bottomNavigationExercise.setNavigationActions(
//            {
//                navigationClickListener.onPrevClick()
//            },
//            {
//                navigationClickListener.onNextClick()
//            }
//        )
//    }

//    private fun fetchExerciseContent(exerciseId: String, forceUpdate: Boolean) {
//        fragmentViewModel.handle(
//            ExerciseFragmentViewModel.ExerciseFragmentViewActions.PulledDownToRefresh(
//                exerciseId,
//                currentStudy.courseId,
//                forceUpdate
//            )
//        )
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ExerciseNavigationClickListener) {
            this.navigationClickListener = context
        }
    }

    interface ExerciseNavigationClickListener {
        fun onPrevClick()
        fun onNextClick()
        fun onMarkCompleteClick()
    }

}