package org.navgurukul.typing.dashboard.beginner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.typing.KeyboardActivity
import org.navgurukul.typing.R
import org.navgurukul.typing.utils.Constants
import org.navgurukul.typing.utils.Logger

class BeginnerFragment : Fragment() {
    private val TAG : String = "BeginnerFragment"
    private lateinit var mListViewBeginner: RecyclerView
    private var mBeginnerListAdapter: BeginnerListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_beginner, container, false)
        mListViewBeginner = view.findViewById(R.id.list_beginner)
        linearLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        mListViewBeginner.layoutManager = linearLayoutManager
        mBeginnerListAdapter = BeginnerListAdapter(requireActivity(), BeginnerListData.getBeginnerData(), object : BeginnerListAdapter.OnItemClickListener {
            override fun onItemClick(item: BeginnerListData?) {}
            override fun onStart(item: BeginnerListData?) {
                val intent = Intent(activity, KeyboardActivity::class.java)
                intent.putExtra("key", item?.key)
                activity!!.startActivityForResult(intent, Constants.REQUEST_CODE_BEGINNER)
            }
        })
        mListViewBeginner.adapter = mBeginnerListAdapter
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment
         * @return A new instance of fragment BeginnerFragment.
         */
        @JvmStatic
        fun newInstance() = BeginnerFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_BEGINNER) {
            val key = data?.getStringExtra("key")
            val wpm = data?.getIntExtra("wpm", 0)
            Logger.d(TAG, " Selected lesson : $key and wpm : $wpm")
            //update beginner list with wpm data
            val list = BeginnerListData.getBeginnerData()
            for (l in list) {
                if (l.key.equals(key)) {
                    l.wpm = wpm!!
                    break
                }
            }
            mBeginnerListAdapter!!.updateBeginnerListData(list)
        }
    }
}