package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.inappmessaging.model.Button
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.R
import org.merakilearn.databinding.FragmentPartnerBinding
import org.merakilearn.datasource.network.model.PartnerDataResponse


class PartnerFragment : Fragment() {
    private lateinit var mBinding: FragmentPartnerBinding
    private val viewModel: OnBoardingViewModel by sharedViewModel()

    companion object {
        const val TAG = "PartnerFragment"
        fun newInstance() = PartnerFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_partner, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(OnBoardingViewActions.GetPartnerData)

        viewModel.viewEvents.observe(viewLifecycleOwner) {

            when (it) {
                is OnBoardingViewEvents.ShowPartnerData -> {
                    partnerData(it.partnerData)

                }

            }
        }
    }

    private fun partnerData(partnerData: PartnerDataResponse) {
        viewPartnerData()
        mBinding.header.text = partnerData.name
        Glide.with(this).load(partnerData.logo).into(mBinding.image)
        mBinding.desc.text = partnerData.description

        mBinding.continueToCourseSelection.setOnClickListener {
            viewModel.handle(OnBoardingViewActions.NavigateNextFromPartnerDataScreen)
        }
    }

    private fun viewPartnerData() {
        mBinding.header.visibility = View.VISIBLE
        mBinding.desc.visibility = View.VISIBLE
        mBinding.image.visibility = View.VISIBLE
        mBinding.continueToCourseSelection.visibility = View.VISIBLE
    }


}