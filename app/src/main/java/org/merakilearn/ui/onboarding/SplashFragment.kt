package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.merakilearn.R
import org.merakilearn.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    companion object {
        fun newInstance() = SplashFragment()
        const val TAG = "SplashFragment"
    }

    private lateinit var mBinding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        return mBinding.root
    }

}