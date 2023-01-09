package org.navgurukul.webide.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_intro.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.extensions.inflate

class IntroFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_intro)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        arguments?.let {
            slideLayout.setBackgroundColor(arguments.getInt("bg"))
            slideImage.setImageResource(arguments.getInt("image"))
            slideTitle.text = arguments.getString("title")
            slideDesc.text = arguments.getString("desc")
        }
    }

    companion object {

        fun newInstance(args: Bundle) = IntroFragment().apply {
            arguments = args
        }
    }
}
