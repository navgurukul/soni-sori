package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.FragmentExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class ExerciseFragment : Fragment() {

    private val fragmentViewModel: ExerciseFragmentViewModel by viewModel(parameters = {
        parametersOf(
            currentStudy.courseId,
            currentStudy.exerciseId
        )
    })
    private lateinit var navigationClickListener: ExerciseNavigationClickListener
    private lateinit var currentStudy: CurrentStudy
    private var isFirst: Boolean = false
    private var isLast: Boolean = false
    private var isCompleted: Boolean = false
    private lateinit var mBinding: FragmentExerciseBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var masterData: MutableList<Exercise> = mutableListOf()
    private val merakiNavigator: MerakiNavigator by inject()

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                showExerciseNavigationBar(this)
            }
        }
    }

    companion object {
        private const val ARG_KEY_CURRENT_STUDY = "arg_current_study"
        private const val ARG_KEY_IS_FIRST_IN_LIST = "arg_is_first"
        private const val ARG_KEY_IS_LAST_IN_LIST = "arg_is_last"
        private const val ARG_KEY_IS_COMPLETED = "arg_is_completed"

        fun newInstance(currentStudy: CurrentStudy, isFirst: Boolean = false , isLast: Boolean = false
        ,isCompleted: Boolean): ExerciseFragment {
            val frag = ExerciseFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_KEY_CURRENT_STUDY, currentStudy)
            bundle.putBoolean(ARG_KEY_IS_FIRST_IN_LIST, isFirst)
            bundle.putBoolean(ARG_KEY_IS_LAST_IN_LIST, isLast)
            bundle.putBoolean(ARG_KEY_IS_COMPLETED, isCompleted)
            frag.arguments = bundle
            return frag
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
        parseIntentData()

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ExerciseFragmentViewModel.ExerciseFragmentViewEvents.ShowToast -> toast(it.toastText)
                is ExerciseFragmentViewModel.ExerciseFragmentViewEvents.ShowExercise -> renderExerciseOnScreen(
                    it.exerciseList
                )
            }
        })

        fragmentViewModel.viewState.observe(viewLifecycleOwner, Observer {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun renderExerciseOnScreen(list: List<Exercise>) {
        if (list.isNotEmpty()) {
            val data = parseDataForContent(list)
            contentAdapter.submitList(data)

            mBinding.recyclerViewSlug.postDelayed( {
                if(isRecyclerScrollable(mBinding.recyclerViewSlug))
                    mBinding.recyclerViewSlug.addOnScrollListener(scrollListener)
                else
                    showExerciseNavigationBar()
            }, 200)
        }
    }

    private fun parseIntentData() {
        if (arguments?.containsKey(ARG_KEY_CURRENT_STUDY) ?: false) {
            currentStudy = requireArguments().getParcelable<CurrentStudy>(ARG_KEY_CURRENT_STUDY)!!
            renderUI()
        }
        if (arguments?.containsKey(ARG_KEY_IS_FIRST_IN_LIST) ?: false) {
            isFirst = requireArguments().getBoolean(ARG_KEY_IS_FIRST_IN_LIST)
        }
        if (arguments?.containsKey(ARG_KEY_IS_LAST_IN_LIST) ?: false) {
            isLast = requireArguments().getBoolean(ARG_KEY_IS_LAST_IN_LIST)
        }
        if (arguments?.containsKey(ARG_KEY_IS_COMPLETED) ?: false) {
            isCompleted = requireArguments().getBoolean(ARG_KEY_IS_COMPLETED)
        }
    }

    private fun renderUI() {
        initContentRV()
        saveCourseExercise()
        initSwipeRefresh()
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.PulledDownToRefresh(currentStudy.exerciseId, currentStudy.courseId,true))
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
        val layoutManager = recyclerView.getLayoutManager() as LinearLayoutManager
        val adapter = recyclerView.getAdapter()
        return if (layoutManager == null || adapter == null) false else layoutManager.findLastCompletelyVisibleItemPosition() < adapter.itemCount - 1
    }

    private fun showExerciseNavigationBar(scrollListener: RecyclerView.OnScrollListener ?= null) {
        mBinding.bottomNavigationExercise.visibility = View.VISIBLE
        scrollListener?.let {
            mBinding.recyclerViewSlug.removeOnScrollListener(it)
        }
        mBinding.bottomNavigationExercise.setView(isCompleted, isFirst, isLast)

        mBinding.bottomNavigationExercise.setMarkAction {
            fragmentViewModel.handle(ExerciseFragmentViewModel.ExerciseFragmentViewActions.MarkCompleteClicked(currentStudy))

            navigationClickListener.onMarkCompleteClick()

            isCompleted = true
            mBinding.bottomNavigationExercise.setView(isCompleted, isFirst, isLast)
        }

        mBinding.bottomNavigationExercise.setNavigationActions(
            {
                navigationClickListener.onPrevClick()
            },
            {
                navigationClickListener.onNextClick()
            }
        )
    }

//    private fun fetchExerciseContent(exerciseId: String, forceUpdate: Boolean) {
//        fragmentViewModel.handle(
//            ExerciseFragmentViewModel.ExerciseFragmentViewActions.PulledDownToRefresh(
//                exerciseId,
//                currentStudy.courseId,
//                forceUpdate
//            )
//        )
//    }

    private fun parseDataForContent(it: List<Exercise>?): List<BaseCourseContent> {
        return it?.firstOrNull()?.content ?: return listOf()
    }

    private fun saveCourseExercise() {
        fragmentViewModel.handle(
            ExerciseFragmentViewModel.ExerciseFragmentViewActions.ScreenRendered(
                currentStudy
            )
        )
    }

    fun setNavigationClickListener(exerciseNavigationListener: ExerciseNavigationClickListener) {
        this.navigationClickListener = exerciseNavigationListener
    }

    interface ExerciseNavigationClickListener {
        fun onPrevClick()
        fun onNextClick()
        fun onMarkCompleteClick()
    }

}