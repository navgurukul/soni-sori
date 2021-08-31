package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.select_course_fragment.*
import org.koin.android.ext.android.inject
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.BaseFragment
import java.io.Serializable

class SelectCourseFragment:BaseFragment() {

    private val navigator: MerakiNavigator by inject()

    companion object{
        fun newInstance(courses_list: List<OnBoardPagesAdapter.PathwayData>, header:String)=SelectCourseFragment().apply {
            arguments= Bundle().apply {
                putString("header",header)
                putSerializable("courses",courses_list as Serializable)
            }
        }
        const val TAG="SelectCourseFragment"
    }
    override fun getLayoutResId(): Int = R.layout.select_course_fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courses=arguments?.getSerializable("courses") as List<OnBoardPagesAdapter.PathwayData>
        setCards(courses)
    }

    private fun setCards(courses_list: List<OnBoardPagesAdapter.PathwayData>) {

        select_course_heading.text=arguments?.getString("header")

        include0.findViewById<TextView>(R.id.course_text).text=courses_list[0].name


        include1.findViewById<TextView>(R.id.course_text).text=courses_list[1].name


        include2.findViewById<TextView>(R.id.course_text).text=courses_list[2].name


        include0.setOnClickListener{
            navigator.openLearnFragment(requireActivity(),true,courses_list[0].name)
            requireActivity().finish()
        }
        include1.setOnClickListener{
            navigator.openLearnFragment(requireActivity(),true,courses_list[1].name)
            requireActivity().finish()
        }
        include2.setOnClickListener{
            navigator.openLearnFragment(requireActivity(),true,courses_list[2].name)
            requireActivity().finish()
        }
    }

}