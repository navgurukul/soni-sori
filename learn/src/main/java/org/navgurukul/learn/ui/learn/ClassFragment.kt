package org.navgurukul.learn.ui.learn

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.learn.R
import org.merakilearn.learn.databinding.FragmentClassBinding
import org.merakilearn.learn.databinding.LayoutClassinfoDialogBinding
import org.merakilearn.learn.databinding.LayoutRevisionDialogBinding
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.dateRange
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.BatchSelectionExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.RevisionClassAdapter
import org.navgurukul.learn.util.toDate


class ClassFragment: Fragment() {
    private val args: CourseContentArgs by fragmentArgs()
    private val fragmentViewModel: ClassFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var mBinding: FragmentClassBinding
    private val merakiNavigator: MerakiNavigator by inject()
    private lateinit var  mRevisionAdapter: RevisionClassAdapter
    private lateinit var mClassAdapter: BatchSelectionExerciseAdapter
    private var screenRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
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


        mBinding.apply {
            revisionList.revisionSelectionSheet.visibility = View.GONE
            classDetail.classCourseDetails.visibility = View.GONE
            batchFragment.batchesInExercise.visibility = View.GONE
            revisionList.revisionClassData.revisionClass.visibility = View.GONE
        }


        initScreenRefresh()

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowToast -> toast(it.toastText)

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowRevisionClasses -> {
                    if(it.revisionClasses.isNotEmpty()){
                        mBinding.revisionList.btnRevision.visibility = View.VISIBLE
                        initRevisionRecyclerView(it.revisionClasses)
                    }else {
                        mBinding.revisionList.btnRevision.visibility = View.GONE
                        toast("No revision classes found at the moment. Please come back later.")
                    }
                    fragmentViewModel.viewState.value?.classContent?.let { it1 -> setupClassHeaderDeatils(it1) }
                    mBinding.apply {
                        revisionList.revisionSelectionSheet.visibility = View.VISIBLE
                        classDetail.classCourseDetails.visibility = View.GONE
                        revisionList.listOfRevision.visibility = View.VISIBLE
                        revisionList.revisionClassData.revisionClass.visibility = View.GONE
                        batchFragment.batchesInExercise.visibility = View.GONE
                    }

                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowRevisionClassToJoin -> {
                    setUpRevisionClassData(it.revisionClass)
                    enrollViewModel.handle(EnrollViewActions.RequestPageLoad(it.revisionClass))
                    fragmentViewModel.viewState.value?.classContent?.let { it1 -> setupClassHeaderDeatils(it1) }
                    mBinding.apply {
                        revisionList.revisionSelectionSheet.visibility = View.VISIBLE
                        classDetail.classCourseDetails.visibility = View.GONE
                        revisionList.revisionClassData.revisionClass.visibility = View.VISIBLE
                        revisionList.listOfRevision.visibility = View.GONE
                        batchFragment.batchesInExercise.visibility = View.GONE
                    }

                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowClassData ->{
                    setUpClassData(it.courseClass)
                    mBinding.apply {
                        enrollViewModel.handle(EnrollViewActions.RequestPageLoad(it.courseClass))
                        classDetail.classCourseDetails.visibility = View.VISIBLE
                        revisionList.revisionSelectionSheet.visibility = View.GONE
                        batchFragment.batchesInExercise.visibility = View.GONE
                    }

                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowBatches ->{
                    initRecyclerViewBatch(it.batches)
                    mBinding.tvClassDetail.visibility= View.GONE
                    mBinding.batchFragment.batchesInExercise.visibility = View.VISIBLE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.OpenLink -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
            }
        }

        fragmentViewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.progressBar.progressBarButton.visibility = if (it.isLoading) View.VISIBLE else View.GONE
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
                is EnrollViewEvents.RefreshContent -> {
                    EnrollViewActions.RequestPageLoad(it.mClass)
                    screenRefreshListener?.onRefresh()
                }
            }
        }

        enrollViewModel.viewState.observe(viewLifecycleOwner){
            mBinding.progressBar.progressBarButton.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            updateState(it)
        }
        learnViewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is LearnFragmentViewEvents.ShowToast -> toast(it.toastText)
                is LearnFragmentViewEvents.EnrolledSuccessfully ->{
                    screenRefreshListener?.onRefresh()
                }

                else -> {}
            }
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
        screenRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            learnViewModel.handle(LearnFragmentViewActions.RefreshCourses)
            fragmentViewModel.handle(ClassFragmentViewModel.ClassFragmentViewActions.RequestContentRefresh)
            mBinding.swipeContainer.isRefreshing = false
        }

        mBinding.swipeContainer.setOnRefreshListener(screenRefreshListener)

        mBinding.errorLayout.btnRefresh.setOnClickListener {
            fragmentViewModel.handle(ClassFragmentViewModel.ClassFragmentViewActions.RequestContentRefresh)
        }
    }

    private fun setupJoinButton(){
        mBinding.batchFragment.joinBatchBtn.setOnClickListener {
            selectedBatch?.let { it1 -> showEnrolDialog(it1) }
        }
    }

    private fun setUpRevisionEnrollBtn(){
        mBinding.revisionList.btnRevision.setOnClickListener {
           selectedRevisionClass?.let { it1 -> showRevisionEnrolDialog(it1) }
        }
    }

    private fun setUpRevisionJoinBtn(revisionClass: CourseClassContent){
        mBinding.revisionList.btnRevision.setOnClickListener {
            val mRevisionClass = revisionClass
            mRevisionClass.let { it1 ->
                enrollViewModel.handle(
                    EnrollViewActions.PrimaryAction(it1, false)
                )
            }
        }
    }

    private fun updateState(it: EnrollViewState) {
        mBinding.apply {
            val button = if (selectedRevisionClass != null || it.type ==  ClassType.revision.name.capitalizeWords() ) revisionList.btnRevision else classDetail.tvBtnJoin

            it.primaryActionBackgroundColor?.let {
                button.setBackgroundColor(it)
            }
            it.primaryAction?.let {
                button.isVisible = true
                button.text = it
            }
        }

    }

    private fun setUpRevisionClassData(revisionClass: CourseClassContent){
        mBinding.revisionList.revisionClassData.apply {
            tvRevDate.text = revisionClass.timeDateRange()
            tvRevFacilatorName.text = revisionClass.facilitator?.name
            setUpRevisionJoinBtn(revisionClass)
            btnDropOut.setOnClickListener {
                showDropoutDialog(revisionClass)
            }
        }

    }


    private fun setUpClassData(courseClass : CourseClassContent){
        mBinding.classDetail.apply {
            setupClassHeaderDeatils(courseClass)
            tvDate.text = courseClass.timeDateRange()
            tvFacilatorName.text = courseClass.facilitator?.name

            tvBtnJoin.setOnClickListener {
                enrollViewModel.handle(EnrollViewActions.PrimaryAction(courseClass, true))
            }
        }

    }

    private fun setupClassHeaderDeatils(courseClass: CourseClassContent) {
        mBinding.revisionList.completeText.text = "Completed on "+courseClass.startTime.toDate()
        mBinding.apply {
            tvSubTitle.text = courseClass.subTitle ?: ""
            tvClassType.text = courseClass.type.name.capitalizeWords()
            tvClassLanguage.text = courseClass.displayableLanguage()
        }

    }


    private fun initRecyclerViewBatch(batches : List<Batch>){
        selectedBatch = batches[0]
        selectedBatch!!.isSelected = true
        mClassAdapter = BatchSelectionExerciseAdapter {
            selectedBatch = it
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        mBinding.batchFragment.recyclerviewBatch.layoutManager = layoutManager
        mBinding.batchFragment.recyclerviewBatch.adapter = mClassAdapter
        mClassAdapter.submitList(batches.take(3))
        setupJoinButton()
    }


    private fun initRevisionRecyclerView(revisionClass: List<CourseClassContent>){
        selectedRevisionClass = revisionClass[0]
        selectedRevisionClass!!.isSelected = true
        mRevisionAdapter = RevisionClassAdapter {
            selectedRevisionClass = it
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.revisionList.recyclerView.layoutManager = layoutManager
        mBinding.revisionList.recyclerView.adapter = mRevisionAdapter
        mRevisionAdapter.submitList(revisionClass)
        setUpRevisionEnrollBtn()
    }


    private fun showEnrolDialog(batch: Batch) {
        val alertLayout: LayoutClassinfoDialogBinding =  LayoutClassinfoDialogBinding.inflate(getLayoutInflater(),null, false)
        val btnAccept: View = alertLayout.btnEnroll
        val btnBack: View = alertLayout.btnback
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setView(alertLayout.root)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvClassTitle = alertLayout.tvClassTitle
        tvClassTitle.text = batch.title
        val tvBatchDate = alertLayout.tvBatchDate
        tvBatchDate.text = batch.dateRange()

        btnAccept.setOnClickListener {
            learnViewModel.handle(LearnFragmentViewActions.PrimaryAction(selectedBatch?.id?:0, true))
            btAlertDialog?.dismiss()
        }
        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45)
    }

    private fun showRevisionEnrolDialog(revisionClass: CourseClassContent) {
        val alertLayout: LayoutRevisionDialogBinding =  LayoutRevisionDialogBinding.inflate(getLayoutInflater(),null, false)
        val btnEnroll: View = alertLayout.btnReviEnroll
        val btnBack: View = alertLayout.btnback
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setView(alertLayout.root)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvBatchDate = alertLayout.tvRevisionDate
        tvBatchDate.text = revisionClass.timeDateRange()
        val tvFacilitatorName = alertLayout.tvFacilatorName
        tvFacilitatorName.text = "Revision Class by "+ revisionClass.facilitator?.name

        btnEnroll.setOnClickListener {
            revisionClass.let { it1 ->
                enrollViewModel.handle(EnrollViewActions.PrimaryAction(it1, false))
            }
            btAlertDialog?.dismiss()
        }
        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45);
    }

    private fun showDropoutDialog(revisionClass: CourseClassContent){

        val alertLayout:View = getLayoutInflater().inflate(R.layout.dialog_dropout, null)
        val btnStay: View = alertLayout.findViewById(R.id.btnStay)
        val btnDroupOut: View = alertLayout.findViewById(R.id.btnDroupOut)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setView(alertLayout)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnStay.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btnDroupOut.setOnClickListener {
            revisionClass.let {
                enrollViewModel.handle(EnrollViewActions.DropOut(it, false))
            }
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45)
    }

}
