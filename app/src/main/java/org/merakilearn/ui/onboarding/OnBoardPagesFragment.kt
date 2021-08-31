package org.merakilearn.ui.onboarding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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
import org.merakilearn.R
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.learn.ui.common.toast
import timber.log.Timber

class OnBoardPagesFragment : BaseFragment() {

    companion object{
        fun newInstance(LANGUAGE: String) =OnBoardPagesFragment().apply {
            arguments= Bundle().apply {
                putString(LANGUAGE_KEY,LANGUAGE)
            }
        }
        const val TAG="OnBoardPagesFragment"
        private const val RC_SIGN_IN=9001
        private const val LANGUAGE_KEY="language_key"
    }
    private lateinit var onBoardPagesAdapter: OnBoardPagesAdapter
    lateinit var list:List<OnBoardPagesAdapter.OnBoardingPageData>

    private val viewModel:WelcomeViewModel by viewModel()

    private val navigator: MerakiNavigator by inject()

    lateinit var LANGUAGE:String

    private lateinit var pathwayList: List<OnBoardPagesAdapter.PathwayData>
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
        LANGUAGE= arguments?.getString(LANGUAGE_KEY).toString()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setItems()
        setIndicator()
        setCurrentIndicator(0)
        skip_login.setOnClickListener{
            viewModel.handle(WelcomeViewActions.InitiateFakeSignUp)
        }
        login_with_google.setOnClickListener{
            signInWithGoogle()
        }

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
                is WelcomeViewEvents.OpenMerakiChat->openMerakiChat(it.roomId)
                is WelcomeViewEvents.OpenCourseSelection->openCourseSelection(pathwayList)
                is WelcomeViewEvents.OpenHomeScreen->openHomeScreen()
            }
        }
    }



    private fun openCourseSelection(courseList: List<OnBoardPagesAdapter.PathwayData>) {
        OnBoardingActivity.launchSelectCourseFragment(requireActivity(),courseList,selectCourseHeader)
        requireActivity().finish()
    }
    private fun openHomeScreen(){
        navigator.openHome(requireContext(),true)
        requireActivity().finish()
    }
    private fun openMerakiChat(roomId:String){
        navigator.openRoom(requireContext(),roomId,true)
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
        try {
            val account=completedTask?.getResult(ApiException::class.java)
            account?.idToken?.let {
                viewModel.handle(WelcomeViewActions.LoginWithAuthToken(it))
            }?:run{
                toast(getString(R.string.unable_to_sign))
            }
        }catch (e: ApiException){
            Timber.e(e,"Google Sign Failed")
            toast(getString(R.string.unable_to_sign))
        }

    }




    private fun setItems() {
        next.setTextColor(Color.parseColor("#48A145"))

        val config= Config()
        config.initialise()
        val res=config.getObjectifiedValue<OnBoardPagesAdapter.OnBoardingPageData>(LANGUAGE)
        next.text=res?.next_text
        skip.text=res?.skip_text
        login_with_google.text=res?.login_with_google_text
        skip_login.text=res?.skip_login_text
        onBoardPagesAdapter= res?.let {
            OnBoardPagesAdapter(it.onBoardingDataList)
        }!!
        viewPager2.adapter=onBoardPagesAdapter

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

        pathwayList=res.onBoardingPathwayList
        selectCourseHeader=res.select_course_header


    }

    private fun setIndicator(){
        val indicators= arrayOfNulls<ImageView>(onBoardPagesAdapter.itemCount)

        val layoutParams: LinearLayout.LayoutParams= LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(5,5,5,5)

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