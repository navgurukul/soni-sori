package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.select_course_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.R
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.BaseFragment

class SelectCourseFragment : BaseFragment() {

    companion object {
        fun newInstance() = SelectCourseFragment()
        val TAG = SelectCourseFragment::class.java.name
    }

    private val viewModel: OnBoardingViewModel by sharedViewModel()

    override fun getLayoutResId(): Int = R.layout.select_course_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, {
            if (it.onBoardingData != null && it.onBoardingTranslations != null) {
                setCards(it.onBoardingData, it.onBoardingTranslations)
            }
        })
    }

    private fun setCards(onBoardingData: OnBoardingData, translations: OnBoardingTranslations) {

        select_course_heading.text = translations.selectCourseHeader

        val padding = resources.getDimensionPixelSize(R.dimen.spacing_4x)
        val width = (resources.displayMetrics.widthPixels - (padding * 2) - padding) / 2
        onBoardingData.onBoardingPathwayList.forEachIndexed { index, pathway ->


            val customView = layoutInflater.inflate(R.layout.course_card, constraint_layout, false)
            customView.id = View.generateViewId()

            customView.findViewById<TextView>(R.id.course_text).text =
                translations.onBoardingPathwayListNames[index]

            val imageView = customView.findViewById<ImageView>(R.id.logo)
            pathway.image.remote?.let {
                GlideApp.with(requireContext())
                    .load(it)
                    .into(imageView)
            } ?: run {
                imageView.setImageResource(DefaultLogos.valueOf(pathway.image.local!!).id)
            }

            constraint_layout.addView(
                customView,
                ConstraintLayout.LayoutParams(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            )
            flow_constraint.referencedIds += customView.id

            customView.setOnClickListener {
                viewModel.handle(OnBoardingViewActions.SelectCourse(pathway.id))
            }

        }
    }

    enum class DefaultLogos(@DrawableRes val id: Int) {
        PYTHON(R.drawable.python_logo),
        TYPING(R.drawable.ic_icon_typing),
        ENGLISH(R.drawable.ic_icon_language),
        JAVASCRIPT(R.drawable.ic_javascript_logo),
        SCRATCH(R.drawable.ic_scratch_cat),
        RESIDENTIAL(R.drawable.residential_icon)
    }

}