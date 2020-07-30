package org.navgurukul.playground.ui.playground

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.playground.R
import org.navgurukul.playground.chaquopy.console.ReplActivity

class PlaygroundFragment : Fragment() {

    private val viewModel: PlaygroundViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playground, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button: Button = view.findViewById(R.id.buttonPythonPlayground)
        button.setOnClickListener { startActivity(Intent(context, ReplActivity::class.java)) }
    }
}