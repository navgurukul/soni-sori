package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.moshi.JsonClass
import kotlinx.android.synthetic.main.select_course_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.datasource.network.model.PathwayData
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.BaseFragment


@JsonClass(generateAdapter = true)
data class SelectCourseFragmentArgs(
    val header: String,
    val courses_list: List<PathwayData>
)
class SelectCourseFragment:BaseFragment() {

    private val navigator: MerakiNavigator by inject()

    companion object{
        fun newInstance(args:SelectCourseFragmentArgs)=SelectCourseFragment().apply{
            this.args=args;
        }
        const val TAG="SelectCourseFragment"
    }

    private  lateinit var args: SelectCourseFragmentArgs
    private val viewModel:WelcomeViewModel by viewModel()
    override fun getLayoutResId(): Int = R.layout.select_course_fragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(args.let { WelcomeViewActions.SetPathWayData(it) })

        viewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is WelcomeViewEvents.ViewPathWayData -> setCards(it.args)
            }
        }
    }

    private fun setCards(args: SelectCourseFragmentArgs) {

        select_course_heading.text= args.header


        for (pathway in args.courses_list){

            val customView=layoutInflater.inflate(R.layout.course_card,null)

            val layoutParams:LinearLayout.LayoutParams=LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.course_card_width),
                resources.getDimensionPixelSize(R.dimen.course_card_height)
            )
            customView.layoutParams=layoutParams
            customView.id=View.generateViewId()
            customView.findViewById<TextView>(R.id.course_text).text=pathway.name

            val thumbnail=GlideApp.with(requireContext())
                .load(R.drawable.python_logo)


            GlideApp.with(requireContext())
                .load(pathway.logo)
                .thumbnail(thumbnail)
                .into(customView.findViewById(R.id.logo))
            constraint_layout.addView(customView)
            flow_constraint.addView(customView)

            customView.setOnClickListener{
                navigator.openLearnFragment(requireActivity(),true,pathway.id)
                requireActivity().finish()
            }

        }
    }

}