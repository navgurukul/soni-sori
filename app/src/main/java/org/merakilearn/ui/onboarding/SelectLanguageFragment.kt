package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.R
import org.merakilearn.databinding.SelectLanguageFragmentBinding
import org.navgurukul.commonui.platform.BaseFragment

class SelectLanguageFragment : BaseFragment() {
    companion object {
        fun newInstance() = SelectLanguageFragment()
        val TAG = SelectLanguageFragment::class.java.name
    }

    private val viewModel: OnBoardingViewModel by sharedViewModel()
    private lateinit var binding : SelectLanguageFragmentBinding

    override fun getLayoutResId(): Int = R.layout.select_language_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnglish.setOnClickListener {
            viewModel.handle(OnBoardingViewActions.SelectLanguage(OnBoardingViewModel.Language.ENGLISH))
        }
        binding.btnHindi.setOnClickListener {
            viewModel.handle(OnBoardingViewActions.SelectLanguage(OnBoardingViewModel.Language.HINDI))
        }
    }
}