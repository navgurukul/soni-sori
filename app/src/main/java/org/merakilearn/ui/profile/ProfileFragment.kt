package org.merakilearn.ui.profile


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_enrolled_batch.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.databinding.FragmentProfileBinding
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.Batches
import org.merakilearn.datasource.network.model.PartnerDataResponse
import org.merakilearn.ui.adapter.SavedFileAdapter
import org.merakilearn.ui.onboarding.OnBoardingActivity
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.learn.ui.common.toast
import org.merakilearn.ui.adapter.EnrolledBatchAdapter
import org.merakilearn.ui.onboarding.OnBoardPagesAdapter
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.ui.learn.ClassFragmentViewModel
import org.navgurukul.learn.ui.learn.LearnFragmentViewActions
import java.io.File

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModel()
    private val merakiNavigator: MerakiNavigator by inject()
    private val userRepo: UserRepo by inject()
    private var screenRefreshListener: SwipeRefreshLayout.OnRefreshListener? = null
    private lateinit var mBinding: FragmentProfileBinding
    private lateinit var mAdapter: EnrolledBatchAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwipeRefresh()

        initShowEnrolledBatches()

        btnPrivacyPolicy.setOnClickListener {
            viewModel.handle(ProfileViewActions.PrivacyPolicyClicked)
        }
        viewModel.viewState.observe(viewLifecycleOwner) {
            it?.let { updateState(it) }
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is ProfileViewEvents.ShowToast -> {
                    toast(it.text)
                }
                is ProfileViewEvents.ShowUpdateServerDialog -> {
                    showUpdateServerDialog(it.serverUrl)
                }
                is ProfileViewEvents.ShareText -> shareCode(it)
                ProfileViewEvents.RestartApp -> OnBoardingActivity.restartApp(
                    requireActivity(),
                    clearNotification = true
                )
                is ProfileViewEvents.OpenUrl -> {
                    merakiNavigator.openCustomTab(it.url, requireContext())
                }
                is ProfileViewEvents.ShowEnrolledBatches -> {
                    mAdapter.submitList(it.batches)
                }
                is ProfileViewEvents.BatchSelectClicked -> {
                    dropOut(it.batch)
                }
                is ProfileViewEvents.ShowPartnerData -> {
                    partnerData(it.partnerData)
                }
            }
        }

        explore_opportunity.setOnClickListener {
            viewModel.handle(ProfileViewActions.ExploreOpportunityClicked)
        }
        mBinding.serverUrlValue.setOnClickListener {
            viewModel.handle(ProfileViewActions.UpdateServerUrlClicked)
        }

        initToolBar()

    }

    private fun initSwipeRefresh() {
        screenRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            viewModel.handle(ProfileViewActions.RefreshPage)
            mBinding.swipeRefreshLayout.isRefreshing = false
        }

        mBinding.swipeRefreshLayout.setOnRefreshListener(screenRefreshListener)

    }

    private fun dropOut(batches: Batches) {
        mBinding.rvEnrolledBatch.btnCross?.setOnClickListener {
            showDropOutDialog(batches)
        }
    }

    private fun partnerData(partnerData: PartnerDataResponse) {
        if (partnerData.name != null && partnerData.description != null && partnerData.logo != null) {
            if (partnerData.websiteLink != null) {
                mBinding.partnerWebsite.visibility = View.VISIBLE
                mBinding.partnerWebsite.text = partnerData.websiteLink
            }
            viewPartnerData()
            mBinding.partnerName.text = partnerData.name
            mBinding.partnerDesc.text = partnerData.description
            Glide.with(this).load(partnerData.logo).into(mBinding.partnerImage)

        }

    }

    private fun viewPartnerData() {
        mBinding.title.visibility = View.VISIBLE
        mBinding.partnerName.visibility = View.VISIBLE
        mBinding.partnerDesc.visibility = View.VISIBLE
        mBinding.partnerImage.visibility = View.VISIBLE

    }

    private fun shareCode(it: ProfileViewEvents.ShareText) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, it.text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(
            sendIntent,
            getString(org.navgurukul.playground.R.string.share_code)
        )
        startActivity(shareIntent)
    }

    private fun showUpdateServerDialog(serverUrl: String) {
        val inputText = EditText(requireContext())
        val alert = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Server Url")
            .setView(inputText)
            .setPositiveButton("OK") { _, _ ->
                viewModel.handle(ProfileViewActions.UpdateServerUrl(inputText.text.toString()))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Reset") { _, _ ->
                viewModel.handle(ProfileViewActions.ResetServerUrl)
            }
            .create()
        alert.setOnShowListener {
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_4x)
            inputText.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd = margin
                marginStart = margin
            }
            inputText.setText(serverUrl)
        }
        alert.show()
    }


    private fun updateState(it: ProfileViewState) {

        it.userName?.let {
            mBinding.tvName.setText(it)
            mBinding.tvName.setSelection(it.length)
        }

        it.userEmail?.let {
            mBinding.tvEmail.setText(it)
        }

        if (it.batches.isEmpty()) {
            mBinding.tvEnrolledText.visibility = View.GONE
            mBinding.rvEnrolledBatch.visibility = View.GONE
        } else {
            mBinding.tvEnrolledText.visibility = View.VISIBLE
            mBinding.rvEnrolledBatch.visibility = View.VISIBLE
        }

        if (it.showEditProfileLayout) {
            mBinding.tvEmail.isFocusable = true
            mBinding.tvEmail.isFocusableInTouchMode = true
            mBinding.tvName.setBackgroundResource(R.drawable.bg_profile_edit_text)
            mBinding.tvName.isFocusable = true
            mBinding.tvName.isFocusableInTouchMode = true
            mBinding.tvName.requestFocus()
            mBinding.btnEdit.text = getString(R.string.save)
            mBinding.btnEdit.setOnClickListener {
                viewModel.handle(
                    ProfileViewActions.UpdateProfile(
                        mBinding.tvName.text.toString(),
                        mBinding.tvEmail.text.toString()
                    )
                )
            }
        } else {
            mBinding.tvEmail.background = null
            mBinding.tvEmail.isFocusable = false
            mBinding.tvName.background = null
            mBinding.tvName.isFocusable = false
            mBinding.btnEdit.text = getString(R.string.edit)
            mBinding.btnEdit.setOnClickListener {
                viewModel.handle(ProfileViewActions.EditProfileClicked)
            }
        }

        mBinding.progressBarButton.isVisible = it.showProgressBar

//        Update profile image only when it changes
        if (mBinding.ivProfile.getTag(R.id.ivProfile) == null ||
            mBinding.ivProfile.getTag(R.id.ivProfile) != it.profilePic
        ) {
            val requestOptions = RequestOptions()
                .centerCrop()
                .transform(CircleCrop())

            val thumbnail = GlideApp.with(this)
                .load(R.drawable.illus_default_avatar)
                .apply(requestOptions)

            GlideApp.with(mBinding.ivProfile)
                .load(it.profilePic)
                .apply(requestOptions)
                .thumbnail(thumbnail)
                .transform(CircleCrop())
                .into(mBinding.ivProfile)

            mBinding.ivProfile.setTag(R.id.ivProfile, it.profilePic)
        }

        if (it.showServerUrl) {
            mBinding.groupServerUrl.visibility = View.VISIBLE
            mBinding.serverUrlValue.text = it.serverUrl
        } else {
            mBinding.groupServerUrl.visibility = View.GONE
        }
    }


    private fun initShowEnrolledBatches() {
        mAdapter = EnrolledBatchAdapter {
            viewModel.selectBatch(it)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvEnrolledBatch.layoutManager = layoutManager
        rvEnrolledBatch.adapter = mAdapter

        rvEnrolledBatch.addItemDecoration(
            SpaceItemDecoration(
                requireContext().resources.getDimensionPixelSize(
                    org.navgurukul.learn.R.dimen.spacing_3x
                ), 0
            )
        )
        rvEnrolledBatch.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                setDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        org.navgurukul.learn.R.drawable.divider
                    )!!
                )
            })
    }

    private fun initToolBar() {
        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.profile),
            R.attr.textPrimary,
            false,
            null,
            null,
            null, null,
            true,
        )


        val view = requireActivity().findViewById<ImageView>(R.id.headerLogOut)
        view.setOnClickListener {
            showLogOutDialog()
        }

    }

    private fun showLogOutDialog() {
        AlertDialog.Builder(requireContext()).setMessage(getString(R.string.want_log_out))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                viewModel.handle(ProfileViewActions.LogOut)
                dialog.dismiss()
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun showDropOutDialog(batches: Batches) {
        val alertLayout: View = getLayoutInflater().inflate(R.layout.dialog_dropout, null)
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
            viewModel.handle(ProfileViewActions.DropOut(batches.id))
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45)
    }

}

