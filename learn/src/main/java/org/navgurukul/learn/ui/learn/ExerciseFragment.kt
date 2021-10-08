package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var currentStudy: CurrentStudy
    private lateinit var mBinding: FragmentExerciseBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var masterData: MutableList<Exercise> = mutableListOf()
    private val merakiNavigator: MerakiNavigator by inject()

    companion object {
        private const val ARG_KEY_CURRENT_STUDY = "arg_current_study"
//        private const val ARG_KEY_COURSE_ID = "arg_course_id"
//        private const val ARG_KEY_COURSE_NAME = "arg_course_name"

        fun newInstance(currentStudy: CurrentStudy): ExerciseFragment {
            val frag = ExerciseFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_KEY_CURRENT_STUDY, currentStudy)
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
        }
    }

    private fun parseIntentData() {
        if (arguments?.containsKey(ARG_KEY_CURRENT_STUDY) ?: false) {
            currentStudy = requireArguments().getParcelable<CurrentStudy>(ARG_KEY_CURRENT_STUDY)!!
            renderUI()
        }
    }

    private fun renderUI() {
        initContentRV()
        saveCourseExercise()
        initSwipeRefresh()
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fetchExerciseContent(currentStudy.exerciseId, true)
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

    private fun fetchExerciseContent(exerciseId: String, forceUpdate: Boolean) {
        fragmentViewModel.handle(
            ExerciseFragmentViewModel.ExerciseFragmentViewActions.FetchExerciseFragmentSlug(
                exerciseId,
                currentStudy.courseId,
                forceUpdate
            )
        )
    }

    private fun parseDataForContent(it: List<Exercise>?): List<BaseCourseContent> {
        return it?.firstOrNull()?.content ?: return listOf()
    }

    private fun saveCourseExercise() {
        fragmentViewModel.handle(
            ExerciseFragmentViewModel.ExerciseFragmentViewActions.SaveCourseExerciseFragmentCurrent(
                currentStudy
            )
        )
    }

}