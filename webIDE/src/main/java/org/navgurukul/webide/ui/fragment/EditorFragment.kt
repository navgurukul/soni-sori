package org.navgurukul.webide.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.navgurukul.webide.R
import org.navgurukul.webide.databinding.FragmentEditorBinding
import org.navgurukul.webide.extensions.string
import org.navgurukul.webide.ui.widget.Editor
import org.navgurukul.webide.util.editor.ResourceHelper
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private var location: String? = null
    private var file: File? = null

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditorBinding.inflate(inflater,container,false)
        location = requireArguments().getString("location")
        file = File(location)
        if (!file!!.exists()) {
            file = null
            val textView = TextView(activity)
            val padding = ResourceHelper.dpToPx(requireActivity(), 48)
            textView.setPadding(padding, padding, padding, padding)
            textView.gravity = Gravity.CENTER
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alert_error, 0, 0, 0)
            textView.setText(R.string.file_problem)
            return textView
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            file?.let {

                val filename = it.name
                binding.fileContent.fileEnding = filename.substringAfterLast('.', "")
                if (filename.endsWith(".html") || filename == "imports.txt") {
//                setSymbol(fileContent, symbolTab, "\t\t")
                    setSymbol(binding.fileContent, binding.symbolOne, "<")
                    setSymbol(binding.fileContent, binding.symbolTwo, "/")
                    setSymbol(binding.fileContent, binding.symbolThree, ">")
                    setSymbol(binding.fileContent, binding.symbolFour, "\"")
                    setSymbol(binding.fileContent, binding.symbolFive, "=")
                    setSymbol(binding.fileContent, binding.symbolSix, "!")
                    setSymbol(binding.fileContent, binding.symbolSeven, "-")
                    setSymbol(binding.fileContent, binding.symbolEight, "/")
                } else if (filename.endsWith(".css")) {
//                setSymbol(fileContent, symbolTab, "\t\t\t\t")
                    setSymbol(binding.fileContent, binding.symbolOne, "{")
                    setSymbol(binding.fileContent, binding.symbolTwo, "}")
                    setSymbol(binding.fileContent, binding.symbolThree, ":")
                    setSymbol(binding.fileContent, binding.symbolFour, ",")
                    setSymbol(binding.fileContent, binding.symbolFive, "#")
                    setSymbol(binding.fileContent, binding.symbolSix, ".")
                    setSymbol(binding.fileContent, binding.symbolSeven, ";")
                    setSymbol(binding.fileContent, binding.symbolEight, "-")
                } else if (filename.endsWith(".js")) {
//                setSymbol(fileContent, symbolTab, "\t\t\t\t")
                    setSymbol(binding.fileContent, binding.symbolOne, "{")
                    setSymbol(binding.fileContent, binding.symbolTwo, "}")
                    setSymbol(binding.fileContent, binding.symbolThree, "(")
                    setSymbol(binding.fileContent, binding.symbolFour, ")")
                    setSymbol(binding.fileContent, binding.symbolFive, "!")
                    setSymbol(binding.fileContent, binding.symbolSix, "=")
                    setSymbol(binding.fileContent, binding.symbolSeven, ":")
                    setSymbol(binding.fileContent, binding.symbolEight, "?")
                }

                val contents = getContents(location!!)
                binding.fileContent.setText(contents)
                //  binding.fileContent.setTextHighlighted(contents)
                binding.fileContent.onTextChangedListener = object : Editor.OnTextChangedListener {
                    override fun onTextChanged(text: String) {
                        try {
                            it.writeText(binding.fileContent.string())
                        } catch (e: IOException) {
                            Timber.wtf(e)
                        }

                    }
                }
            }
        }
    }


    private fun setSymbol(editor: Editor, button: Button?, symbol: String) {
        button!!.text = symbol
        button.setOnClickListener(SymbolClickListener(editor, symbol))
    }

    private fun setSymbol(editor: Editor, button: ImageButton?, symbol: String) {
        button!!.setOnClickListener(SymbolClickListener(editor, symbol))
    }

    private fun getContents(location: String): String {
        try {
            return FileInputStream(location).bufferedReader().use(BufferedReader::readText)
        } catch (e: Exception) {
            Timber.e(e)
        }

        return "Unable to read file!"
    }

    private inner class SymbolClickListener
    internal constructor(private val editor: Editor, private val symbol: String) : View.OnClickListener {

        override fun onClick(v: View) {
            val start = Math.max(editor.selectionStart, 0)
            val end = Math.max(editor.selectionEnd, 0)
            editor.text.replace(Math.min(start, end), Math.max(start, end),
                    symbol, 0, symbol.length)
        }
    }

    companion object {

        fun newInstance(args: Bundle) = EditorFragment().apply {
            arguments = args
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
