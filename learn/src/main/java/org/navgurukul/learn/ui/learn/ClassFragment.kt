package org.navgurukul.learn.ui.learn

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.batches_in_exercise.*
import kotlinx.android.synthetic.main.class_course_detail.*
import kotlinx.android.synthetic.main.fragment_class.*
import kotlinx.android.synthetic.main.item_batch_exercise.*
import kotlinx.android.synthetic.main.layout_classinfo_dialog.view.*
import kotlinx.android.synthetic.main.revision_selection_sheet.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.dateRange
import org.navgurukul.learn.databinding.FragmentClassBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.BatchSelectionExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.RevisionClassAdapter


class ClassFragment: Fragment() {
    private val args: CourseContentArgs by fragmentArgs()
    private val fragmentViewModel: ClassFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var mBinding: FragmentClassBinding
    private val merakiNavigator: MerakiNavigator by inject()
    private lateinit var  mRevisionAdapter: RevisionClassAdapter
    private lateinit var mClassAdapter: BatchSelectionExerciseAdapter
    private val learnViewModel: LearnFragmentViewModel by sharedViewModel()
    private var selectedBatch : Batch? = null
    private var selectedClass: CourseClassContent? = null
    private var selectedRevisionClass : CourseClassContent? = null
    private val enrollViewModel : EnrollViewModel by sharedViewModel()


    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            classId: String,
            courseContentType: CourseContentType,
        ): ClassFragment {
            return ClassFragment().apply {
                arguments = CourseContentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    classId,
                    courseContentType
                ).toBundle()
            }
        }

        const val TAG = "ClassFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_class, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.revisionList.visibility = View.GONE
        mBinding.classDetail.visibility = View.GONE
        mBinding.batchFragment.visibility = View.GONE
        initScreenRefresh()

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowToast -> toast(it.toastText)

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowRevisionClasses -> {
                    initRevisionRecyclerView(it.revisionClasses)
                    fragmentViewModel.viewState.value?.classContent?.let { it1 -> setupClassHeaderDeatils(it1) }
                    mBinding.revisionList.visibility = View.VISIBLE
                    mBinding.classDetail.visibility = View.GONE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowRevisionClassToJoin -> {
                    setUpRevisionJoinBtn(it.revisionClass)
                    fragmentViewModel.viewState.value?.classContent?.let { it1 -> setupClassHeaderDeatils(it1) }
                    mBinding.revisionList.visibility = View.GONE
                    mBinding.classDetail.visibility = View.GONE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowClassData ->{
                    setUpClassData(it.courseClass)
                    mBinding.classDetail.visibility = View.VISIBLE
                    mBinding.revisionList.visibility = View.GONE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowBatches ->{
                    initRecyclerViewBatch(it.batches)
                    mBinding.tvClassDetail.visibility= View.GONE
                    mBinding.batchFragment.visibility = View.VISIBLE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.OpenLink -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
            }
        }

        fragmentViewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            showErrorScreen(it.isError)
        }
        enrollViewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is EnrollViewEvents.ShowToast -> toast(it.toastText)
                is EnrollViewEvents.OpenLink -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )

            }
        }
        enrollViewModel.viewState.observe(viewLifecycleOwner){
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            updateState(it)
        }

    }

    private fun showErrorScreen(isError: Boolean) {
        if (isError) {
            mBinding.errorLayout.root.visibility = View.VISIBLE
            mBinding.tvClassDetail.visibility = View.GONE
        } else {
            mBinding.errorLayout.root.visibility = View.GONE
            mBinding.tvClassDetail.visibility = View.VISIBLE
        }
    }
    private fun initScreenRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fragmentViewModel.handle(ClassFragmentViewModel.ClassFragmentViewActions.RequestContentRefresh)
            mBinding.swipeContainer.isRefreshing = false
        }

        mBinding.errorLayout.btnRefresh.setOnClickListener {
            fragmentViewModel.handle(ClassFragmentViewModel.ClassFragmentViewActions.RequestContentRefresh)
        }
    }

    private fun setupJoinButton(){
        joinBatchBtn.setOnClickListener {
            selectedBatch?.let { it1 -> showEnrolDialog(it1) }
        }
    }

    private fun setUpRevisionJoinBtn(revisionClass: CourseClassContent ?= null){
        btnRevision.setOnClickListener {
            val mRevisionClass = revisionClass ?: selectedRevisionClass
            mRevisionClass?.let { it1 ->
                enrollViewModel.handle(
                    EnrollViewActions.PrimaryAction(it1)
                )
            } ?: kotlin.run { toast("Please Select a Class to Enrol into") }
        }
    }

    private fun updateState(it: EnrollViewState) {
        val button = if (selectedRevisionClass != null) btnRevision else tvBtnJoin

        it.primaryActionBackgroundColor?.let {
            button.setBackgroundColor(it)
        }
        it.primaryAction?.let {
            button.isVisible = true
            button.text = it
        }
    }

    private fun setUpClassData(courseClass : CourseClassContent){
        setupClassHeaderDeatils(courseClass)
        tvDate.text = courseClass.timeDateRange()
        tvFacilatorName.text = courseClass.facilitator?.name

        tvBtnJoin.setOnClickListener {
            enrollViewModel.handle(EnrollViewActions.PrimaryAction(courseClass))
        }
    }

    private fun setupClassHeaderDeatils(courseClass: CourseClassContent) {
        tvSubTitle.text = courseClass.subTitle
        tvClassType.text = courseClass.type.name.capitalizeWords()
        tvClassLanguage.text = courseClass.displayableLanguage()
    }

    private fun initRecyclerViewBatch(batches : List<Batch>){
        mClassAdapter = BatchSelectionExerciseAdapter {
            selectedBatch = it
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        recyclerviewBatch.layoutManager = layoutManager
        recyclerviewBatch.adapter = mClassAdapter
        mClassAdapter.submitList(batches.subList(0,4))
        setupJoinButton()
    }

    private fun initRevisionRecyclerView(revisionClass: List<CourseClassContent>){
        mRevisionAdapter = RevisionClassAdapter {
            selectedRevisionClass = it
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = mRevisionAdapter
        mRevisionAdapter.submitList(revisionClass)
        setUpRevisionJoinBtn()
    }

    private fun showEnrolDialog(batch: Batch) {
        val alertLayout: View =  getLayoutInflater().inflate(R.layout.layout_classinfo_dialog, null)
        val btnAccept: View = alertLayout.findViewById(R.id.btnEnroll)
        val btnBack: View = alertLayout.findViewById(R.id.btnback)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setView(alertLayout)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvClassTitle = alertLayout.tvClassTitle
        tvClassTitle.text = batch.title
        val tvBatchDate = alertLayout.tv_Batch_Date
        tvBatchDate.text = batch.dateRange()

        btnAccept.setOnClickListener {
            learnViewModel.handle(LearnFragmentViewActions.PrimaryAction(selectedBatch?.id?:0))
            btAlertDialog?.dismiss()
        }
        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45);
    }

}