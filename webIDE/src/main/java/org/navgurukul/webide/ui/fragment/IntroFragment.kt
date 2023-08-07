package org.navgurukul.webide.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.navgurukul.webide.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentIntroBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        arguments?.let {
            binding.slideLayout.setBackgroundColor(arguments.getInt("bg"))
            binding.slideImage.setImageResource(arguments.getInt("image"))
            binding.slideTitle.text = arguments.getString("title")
            binding.slideDesc.text = arguments.getString("desc")
        }
    }

    companion object {

        fun newInstance(args: Bundle) = IntroFragment().apply {
            arguments = args
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
