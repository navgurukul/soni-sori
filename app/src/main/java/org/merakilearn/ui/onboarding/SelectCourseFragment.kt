package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.select_course_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.R
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.SvgLoader

class SelectCourseFragment : BaseFragment() {

    companion object {
        fun newInstance() = SelectCourseFragment()
        val TAG = SelectCourseFragment::class.java.name
    }

    private val viewModel: OnBoardingViewModel by sharedViewModel()

    override fun getLayoutResId(): Int = R.layout.select_course_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, { state ->
            if (state.onBoardingData != null && state.onBoardingTranslations != null) {
                setCards(state.onBoardingData, state.onBoardingTranslations)
            }
        })

        viewModel.loadPathways()
    }

    private val addedPathwayIds = mutableSetOf<Int>()

        private fun setCards(onBoardingData: OnBoardingData, translations: OnBoardingTranslations) {
        select_course_heading.text = translations.selectCourseHeader

        val padding = resources.getDimensionPixelSize(R.dimen.spacing_4x)
        val width = (resources.displayMetrics.widthPixels - (padding * 2) - padding) / 2

        onBoardingData.onBoardingPathwayList.forEach { pathway ->
            if (!addedPathwayIds.contains(pathway.id)) {
                val customView =
                    layoutInflater.inflate(R.layout.course_card, constraint_layout, false)
                customView.id = View.generateViewId()

                val courseText = customView.findViewById<TextView>(R.id.course_text)
                courseText.text = pathway.name
                val imageView = customView.findViewById<ImageView>(R.id.logo)
                pathway.logo?.let { svgUrl ->
                    SvgLoader(requireContext()).loadSvgFromUrl(svgUrl, imageView)
                }

                constraint_layout.addView(
                    customView,
                    ConstraintLayout.LayoutParams(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                )
                flow_constraint.referencedIds += customView.id

                customView.setOnClickListener {
                    viewModel.handle(OnBoardingViewActions.SelectCourse(pathway.id))
                }

                addedPathwayIds.add(pathway.id)

                customView.setOnClickListener {
                    viewModel.handle(OnBoardingViewActions.SelectCourse(pathway.id))
                }
            }
        }
    }
}