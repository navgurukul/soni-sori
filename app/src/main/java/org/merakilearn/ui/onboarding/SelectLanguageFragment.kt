package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.select_language_fragment.*
import org.merakilearn.OnBoardingActivity
import org.merakilearn.R
import org.navgurukul.commonui.platform.BaseFragment

data class SelectLanguageFragmentArgs(
    val language:String)

class SelectLanguageFragment : BaseFragment() {
    companion object {
        fun newInstance()=SelectLanguageFragment()
        const val ON_BOARDING_ENGLISH = "OnBoarding_English"
        const val ON_BOARDING_HINDI = "OnBoarding_Hindi"
        const val TAG="SelectLanguageFragment"
    }
    override fun getLayoutResId(): Int = R.layout.select_language_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        english.setOnClickListener{
            OnBoardingActivity.launchWelcomeFragment(requireActivity(), SelectLanguageFragmentArgs(language = ON_BOARDING_ENGLISH))
            requireActivity().finish()
        }
        hindi.setOnClickListener{
            OnBoardingActivity.launchWelcomeFragment(requireActivity(), SelectLanguageFragmentArgs(language = ON_BOARDING_HINDI))
            requireActivity().finish()
        }
    }
}