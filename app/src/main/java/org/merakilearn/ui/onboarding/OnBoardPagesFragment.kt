package org.merakilearn.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.on_board_pages_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.OnBoardingActivity
import org.merakilearn.OnBoardingActivityArgs
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.datasource.network.model.OnBoardingPageData
import org.merakilearn.datasource.network.model.PathwayData
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.learn.ui.common.toast

class OnBoardPagesFragment : BaseFragment() {

    companion object{
        fun newInstance(args:OnBoardingActivityArgs) =OnBoardPagesFragment().apply{
            language=args.language_key
        }
        const val TAG="OnBoardPagesFragment"
        private const val RC_SIGN_IN=9001
    }
    private lateinit var onBoardPagesAdapter: OnBoardPagesAdapter
    lateinit var list:List<OnBoardingPageData>

    private val viewModel:OnBoardingViewModel by viewModel()

    private val navigator: MerakiNavigator by inject()

    lateinit var language:String

    private lateinit var pathwayList: List<PathwayData>
    private lateinit var selectCourseHeader:String

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build())
    }
    override fun getLayoutResId(): Int= R.layout.on_board_pages_fragment

    override fun onAttach(context: Context) {

        super.onAttach(context)
        activity?.let {
            if ((Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)){
                it.window.statusBarColor = ContextCompat.getColor(it, R.color.colorWhite)
            }
            else{
                it.window.statusBarColor = ContextCompat.getColor(it, R.color.primaryDarkColor)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        skip_login.setOnClickListener{
            viewModel.handle(WelcomeViewActions.InitiateFakeSignUp)
        }
        login_with_google.setOnClickListener{
            signInWithGoogle()
        }

        viewModel.handle(WelcomeViewActions.RetrieveDataFromConfig(language))

        viewModel.viewState.observe(viewLifecycleOwner){
            progress_bar_button.visibility=if(it.isLoading) View.VISIBLE else View.GONE
            it.initialSyncProgress?.let { progressState->
                showLoading(getString(progressState.statusText))
            }?: run{
                dismissLoadingDialog()
            }
        }

        viewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is WelcomeViewEvents.ShowToast-> toast(it.toastText)
                is WelcomeViewEvents.OpenCourseSelection->openCourseSelection(pathwayList)
                is WelcomeViewEvents.OpenHomeScreen->openHomeScreen()
                is WelcomeViewEvents.DisplayData -> displayData(it.data)
            }
        }
    }

        private fun displayData(data: OnBoardingPageData?){


            next.text=data?.next_text
            skip.text=data?.skip_text
            login_with_google.text=data?.login_with_google_text
            skip_login.text=data?.skip_login_text
            onBoardPagesAdapter= data?.let {
                OnBoardPagesAdapter(it.onBoardingDataList,requireContext())
            }!!

            viewPager2.adapter=onBoardPagesAdapter

            pathwayList=data.onBoardingPathwayList
            selectCourseHeader=data.select_course_header
            viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })
            viewPager2.isUserInputEnabled=false

            (viewPager2.getChildAt(0) as RecyclerView).overScrollMode= RecyclerView.OVER_SCROLL_NEVER

            skip.setOnClickListener {
                viewPager2.currentItem=onBoardPagesAdapter.itemCount
            }

            next.setOnClickListener{
                viewPager2.currentItem+=1
            }

            setIndicator()
            setCurrentIndicator(0)

        }

    private fun openCourseSelection(courseList: List<PathwayData>) {

        OnBoardingActivity.launchSelectCourseFragment(requireActivity(),
            selectCourseHeader,courseList)
        requireActivity().finish()
    }
    private fun openHomeScreen(){
        navigator.openHome(requireContext(),true)
        requireActivity().finish()
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener{
            startActivityForResult(googleSignInClient.signInIntent,RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {

            val account=completedTask?.getResult(ApiException::class.java)
            account?.idToken?.let {
                viewModel.handle(WelcomeViewActions.LoginWithAuthToken(it))
            }?:run{
                toast(getString(R.string.unable_to_sign))
            }

    }

    private fun setIndicator(){
        val indicators= arrayOfNulls<ImageView>(onBoardPagesAdapter.itemCount)

        val layoutParams: LinearLayout.LayoutParams= LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(resources.getDimensionPixelSize(R.dimen.on_boarding_indicator_margin))

        for(i in indicators.indices){
            indicators[i]= ImageView(requireContext())
            indicators[i].let {
                it?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.onboard_ellipse_inactive
                    )
                )
                it?.layoutParams=layoutParams
                indicator_layout.addView(it)

            }
        }

    }

    fun setCurrentIndicator(position:Int){
        if(position==indicator_layout.childCount-1){
            login_button_layout.visibility= View.VISIBLE
            indicator_layout.visibility= View.GONE
            skip.visibility= View.GONE
            next.visibility= View.GONE

        }
        else {
            val count = indicator_layout.childCount;
            for (i in 0 until count) {
                val imageView = indicator_layout.getChildAt(i) as ImageView
                if (i == position) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.onboard_ellipsebg
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.onboard_ellipse_inactive
                        )
                    )
                }

            }
        }
    }
}