package org.navgurukul.learn.ui.learn

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.batch_card.*
import kotlinx.android.synthetic.main.generated_certificate.view.*
import kotlinx.android.synthetic.main.layout_classinfo_dialog.view.*
import kotlinx.android.synthetic.main.upcoming_class_selection_sheet.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.views.EmptyStateView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.ClassType
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.dateRange
import org.navgurukul.learn.courses.network.model.sanitizedType
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.ui.learn.adapter.DotItemDecoration
import org.navgurukul.learn.ui.learn.adapter.UpcomingEnrolAdapater
import org.navgurukul.learn.util.BrowserRedirectHelper
import org.navgurukul.learn.util.toDate
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LearnFragment : Fragment(){

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    private lateinit var mClassAdapter: UpcomingEnrolAdapater
    private var screenRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
    private val merakiNavigator: MerakiNavigator by inject()
    lateinit var pdfView: PDFView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_learn, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        mBinding.progressBarButton.visibility = View.VISIBLE
        mBinding.emptyStateView.state = EmptyStateView.State.NO_CONTENT

        initSwipeRefresh()

        configureToolbar()

        viewModel.handle(LearnFragmentViewActions.RequestPageLoad)
        viewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.swipeContainer.isRefreshing = false
            mBinding.progressBarButton.isVisible = it.loading
            mCourseAdapter.submitList(it.courses, it.logo)
            configureToolbar(
                it.subtitle,
                it.pathways.isNotEmpty(),
                it.selectedLanguage,
                it.languages.isNotEmpty(),
                it.logo
            )
            mBinding.emptyStateView.isVisible = !it.loading && it.courses.isEmpty()
            mBinding.layoutTakeTest.isVisible = it.showTakeTestButton
            if(!it.classes.isEmpty()){
                mBinding.upcoming.root.isVisible = true
                initUpcomingRecyclerView(it.classes)
                mBinding.enrolledButFinished.root.isVisible = false
            }else{
                mBinding.upcoming.root.isVisible = false
            }
            if(!it.batches.isEmpty()){
                mBinding.batchCard.root.isVisible = true
                setUpUpcomingData(it.batches.first())
            }else{
                mBinding.batchCard.root.isVisible = false
            }

            if (it.showTakeTestButton && it.currentPathwayIndex > -1 && it.currentPathwayIndex < it.pathways.size)
                showTestButton(it.pathways[it.currentPathwayIndex].cta!!)

            if (it.shouldShowCertificate == true){
                mBinding.certificate.visibility = View.VISIBLE
            }else {
                mBinding.certificate.visibility = View.GONE
            }
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is LearnFragmentViewEvents.OpenCourseDetailActivity -> {
                    CourseContentActivity.start(requireContext(), it.courseId, it.pathwayId)
                }
                LearnFragmentViewEvents.OpenPathwaySelectionSheet -> {
                    LearnFragmentPathwaySelectionSheet().show(
                        parentFragmentManager,
                        "OpenPathwaySelectionSheet"
                    )
                }
                LearnFragmentViewEvents.OpenLanguageSelectionSheet -> {
                    LearnLanguageSelectionSheet().show(
                        parentFragmentManager,
                        "OpenLanguageSelectionSheet"
                    )
                }
                 is LearnFragmentViewEvents.OpenBatchSelectionSheet -> {
                    LearnBatchSelectionSheet().show(
                        parentFragmentManager,
                        "LearnBatchesSelectionSheet"
                    )
                }
                is LearnFragmentViewEvents.BatchSelectClicked ->{
                    showEnrolDialog(it.batch)
                }
                is LearnFragmentViewEvents.ShowUpcomingBatch ->{
                    setUpUpcomingData(it.batch)
                    mBinding.batchCard.root.visibility = View.VISIBLE
                    mBinding.upcoming.root.visibility = View.GONE
                }
                is LearnFragmentViewEvents.ShowUpcomingClasses ->{
                    initUpcomingRecyclerView(it.classes)
                    mBinding.upcoming.root.visibility = View.VISIBLE
                    mBinding.batchCard.root.visibility = View.GONE
                    mBinding.enrolledButFinished.root.visibility = View.GONE

                }
                is LearnFragmentViewEvents.ShowCompletedStatus ->{
                    mBinding.enrolledButFinished.root.visibility = View.VISIBLE
                    mBinding.upcoming.root.visibility = View.GONE
                }
                is LearnFragmentViewEvents.GetCertificate -> {
                    getCertificate(it.pdfUrl,it.getCompletedPortion)
                }
                is LearnFragmentViewEvents.ShowToast -> toast(it.toastText)
                is LearnFragmentViewEvents.OpenUrl -> {
                    it.cta?.let { cta ->
                        if (cta.url.contains(BrowserRedirectHelper.WEBSITE_REDIRECT_URL_DELIMITER))
                            merakiNavigator.openCustomTab(
                                BrowserRedirectHelper.getRedirectUrl(requireContext(), cta.url)
                                    .toString(),
                                requireContext()
                            )
                        else
                            merakiNavigator.openDeepLink(
                                requireActivity(),
                                cta.url
                            )
                    }
                }
                else -> {
                }

            }
        }
    }

    private fun getCertificate(pdfUrl : String, completedPortion: Int){
        mBinding.certificate.setOnClickListener {
            if (completedPortion != 100){
                val dialog = BottomSheetDialog(requireContext())
                val view = layoutInflater.inflate(R.layout.generated_certificate, null)
                pdfView = view.idPDFView
                view.tvDownload.setOnClickListener {
                    generatePDF(pdfUrl)
                }
                view.tvShare.setOnClickListener {
                    showShareIntent(pdfUrl)
                }
                RetrievePDFFromURL(pdfView).execute(pdfUrl)
                println("required completed portion in fragment $completedPortion")
                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            } else {
                println("required completed portion in fragment $completedPortion")
                Toast.makeText(requireContext(), R.string.complete_course, Toast.LENGTH_LONG).show()
            }

        }
    }

    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {
        @SuppressLint("StaticFieldLeak")
        val mypdfView: PDFView = pdfView
        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(params.get(0))
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null;
            }
            return inputStream;
        }
        override fun onPostExecute(result: InputStream?) {
            mypdfView.fromStream(result).load()
            }
        }

    private fun generatePDF(pdfUrl : String){
        val download= context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val PdfUri = Uri.parse(pdfUrl)
        val getPdf = DownloadManager.Request(PdfUri)
        getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        download.enqueue(getPdf)
        Toast.makeText(context,"Download Started", Toast.LENGTH_LONG).show()

    }

    private fun showShareIntent(pdfUrl:String) {
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "dhanu"
        )

        val pdfUri : Uri = FileProvider.getUriForFile(
                requireContext(),
                "org.merakilearn.provider_paths",
                outputFile
            )

//        Log.d("pdfUrl","$pdfUrl")
        Log.d("uritext","$pdfUri")
        val url = requireContext().contentResolver.getType(pdfUri)
        val extention = MimeTypeMap.getSingleton().getExtensionFromMimeType(url)
        Log.d("geturl", "$url")
        Log.d("getExtenstion","$extention")
//        } else {
//            pdfUri = Uri.fromFile(outputFile)
//        }
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "application/pdf"
        val pdf = Uri.parse(pdfUrl)
        share.putExtra(Intent.EXTRA_TEXT,pdf)
        share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(share, "Share"))

    }


    private fun setUpUpcomingData(batch: Batch) {
        tvType.text =batch.sanitizedType()+" :"
        tvTitleBatch.text = batch.title
        tvBatchDate.text = batch.dateRange()
        tvText.text = "Can't start on "+ batch.startTime?.toDate()
        tvBtnEnroll.setOnClickListener {
            showEnrolDialog(batch)
        }
        more_classe.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.BtnMoreBatchClicked)
        }
    }

    private fun showTestButton(cta: PathwayCTA) {
        mBinding.buttonTakeTest.text = cta.value
        mBinding.buttonTakeTest.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.PathwayCtaClicked)
        }
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
            viewModel.handle(LearnFragmentViewActions.PrimaryAction(batch.id?:0, true))
            btAlertDialog?.dismiss()
        }
        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45);
    }

    private fun configureToolbar(
        subtitle: String? = null, attachClickListener: Boolean = false,
        selectedLanguage: String? = null, languageClickListener: Boolean = false, pathwayIcon : String? = null
    ) {
        (activity as? ToolbarConfigurable)?.let {
            if (subtitle != null) {
                it.configure(
                    subtitle,
                    R.attr.textPrimary,
                    subtitle="",
                    onClickListener = if (attachClickListener) {
                        {
                            viewModel.handle(LearnFragmentViewActions.ToolbarClicked)
                        }

                    } else null,
                    action = selectedLanguage,
                    actionOnClickListener = if (languageClickListener) {
                        {
                            viewModel.handle(LearnFragmentViewActions.LanguageSelectionClicked)
                        }
                    } else null,
                    showPathwayIcon = true,
                    pathwayIcon = pathwayIcon
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        screenRefreshListener?.onRefresh()
    }

    private fun initSwipeRefresh() {
        screenRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            viewModel.handle(LearnFragmentViewActions.RefreshCourses)
            mBinding.swipeContainer.isRefreshing = false
        }
        mBinding.swipeContainer.setOnRefreshListener(screenRefreshListener)
    }

    private fun initUpcomingRecyclerView(upcomingClassList: List<CourseClassContent>){
        mClassAdapter = UpcomingEnrolAdapater{
            val viewState = viewModel.viewState.value
            viewState?.let { state ->
                val pathwayId = state.pathways[state.currentPathwayIndex].id
                if(it.type == ClassType.doubt_class)
                    ClassActivity.start(requireContext(), it)
                else if(it.type == ClassType.revision)
                    CourseContentActivity.start(requireContext(), it.courseId, pathwayId, it.parentId?:it.id)
                else{
                    CourseContentActivity.start(requireContext(), it.courseId, pathwayId, it.id)
                }
            }
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager = layoutManager
        recyclerViewUpcoming.adapter = mClassAdapter

        mClassAdapter.submitList(upcomingClassList)
    }

    private fun initRecyclerView() {
        mCourseAdapter = CourseAdapter {
            viewModel.selectCourse(it)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourse.layoutManager = layoutManager
        mBinding.recyclerviewCourse.adapter = mCourseAdapter
        mBinding.recyclerviewCourse.addItemDecoration(
            DotItemDecoration(requireContext())
        )
    }

}
