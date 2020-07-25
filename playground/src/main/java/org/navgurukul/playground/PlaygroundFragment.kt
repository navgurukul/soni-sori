package org.navgurukul.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class PlaygroundFragment : Fragment() {

    private lateinit var playgroundViewModel: PlaygroundViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        playgroundViewModel =
            ViewModelProviders.of(this).get(PlaygroundViewModel::class.java)

      return inflater.inflate(R.layout.fragment_playground, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.text)
        playgroundViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }
}