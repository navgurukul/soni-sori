package org.navgurukul.chat.features.home.room.list

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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

    private val viewModel: RoomListViewModel by viewModel(parameters = { parametersOf(RoomListViewState()) })
    private val navigator: MerakiNavigator by inject()
    private val roomController: RoomSummaryController by inject()
    private val id="!mpKUsizojzHSvioqKx:navgurukul.org";
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar?.visibility = View.VISIBLE;


            navigator.openRoom(requireActivity(), id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(org.navgurukul.chat.R.layout.fragment_room_bot, container, false)
        // Inflate the layout for this fragment
        progressBar = view.findViewById<View>(org.navgurukul.chat.R.id.progressBar) as  ProgressBar

        return inflater.inflate(org.navgurukul.chat.R.layout.fragment_room_bot, container, false)
    }

    private fun handleSelectRoom(event: String) {
        progressBar?.visibility = View.GONE;
        navigator.openRoom(requireActivity(), event)
    }




}