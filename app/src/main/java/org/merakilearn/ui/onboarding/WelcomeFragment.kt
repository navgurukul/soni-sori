package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.MainActivity
import org.merakilearn.R
import org.merakilearn.databinding.FragmentWelcomeBinding
import org.merakilearn.ui.common.toast
import org.merakilearn.util.AppUtils


class WelcomeFragment : Fragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
        const val TAG = "WelcomeFragment"
    }

    private lateinit var mBinding: FragmentWelcomeBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.tvAlready.setOnClickListener {
            AppUtils.changeFragment(
                parentFragmentManager,
                LoginFragment.newInstance(),
                R.id.fragment_container_onboarding,
                true,
                LoginFragment.TAG
            )
        }

        mBinding.tvStarted.setOnClickListener {
            mBinding.progressBarButton.visibility = View.VISIBLE
            viewModel.initFakeSignUp().observe(viewLifecycleOwner, Observer {
                mBinding.progressBarButton.visibility = View.GONE
                if (it) {
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    toast(getString(R.string.please_login))
                }
            })

        }
    }

}