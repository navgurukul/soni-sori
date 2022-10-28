package org.navgurukul.chat.features.home.room.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.koin.android.ext.android.inject
import org.merakilearn.core.navigator.MerakiNavigator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [roomBot.newInstance] factory method to
 * create an instance of this fragment.
 */
class roomBot : Fragment() {
    private val navigator: MerakiNavigator by inject()
//    private val id="!SkrjbvgZuKEtVJqZNA:navgurukul.org";
    private val id="!mpKUsizojzHSvioqKx:navgurukul.org";
    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                progressBar?.visibility = View.VISIBLE;


//            navigator.openRoom(requireActivity(), id)
//                activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Toast.makeText(activity?.applicationContext, "Clicked back button", Toast.LENGTH_LONG).show()
//            }
//        })
        handleSelectRoom(id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(org.navgurukul.chat.R.layout.fragment_room_bot, container, false)
        // Inflate the layout for this fragment



        return inflater.inflate(org.navgurukul.chat.R.layout.fragment_room_bot, container, false)
    }

    private fun handleSelectRoom(event: String) {
        progressBar?.visibility = View.GONE;
        navigator.openRoom(requireActivity(), event)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()

            }
        })
    }
    fun onBackPressed() {
        val fm: FragmentManager = requireActivity().supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        Log.d("CDA", "onBackPressed Called")
        val setIntent = Intent(Intent.ACTION_MAIN)
        setIntent.addCategory(Intent.CATEGORY_HOME)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(setIntent)
        return
    }


}