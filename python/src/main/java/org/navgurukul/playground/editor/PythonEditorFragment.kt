package org.navgurukul.playground.editor

import android.content.Intent
import android.os.Bundle
import android.text.style.CharacterStyle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toast
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.playground.R
import org.navgurukul.playground.custom.addTextAtCursorPosition

class PythonEditorFragment : BaseFragment() {

    private val viewModel: PythonEditorViewModel by viewModel(parameters = {
        parametersOf(
            pythonEditorArgs, ContextCompat.getColor(requireContext(), R.color.error_text)
        )
    })

    private val pythonEditorArgs: PythonEditorArgs by fragmentArgs()

    private val etInput: EditText by lazy { requireView().findViewById(R.id.etInput) }
    private val tvOutput: TextView by lazy { requireView().findViewById(R.id.tvOutput) }
    private val etCode: EditText by lazy { requireView().findViewById(R.id.etCode) }
    private val layoutInput: View by lazy { requireView().findViewById(R.id.layoutInput) }
    private val controlsContainer: ViewGroup by lazy { requireView().findViewById(R.id.control_buttons_container) }
    private val ibEnter: MaterialButton by lazy { requireView().findViewById(R.id.ibEnter) }
    private val sheetBehavior: BottomSheetBehavior<View> by lazy {
        BottomSheetBehavior.from(
            bottomSheet
        )
    }
    private val bottomSheet: View by lazy { requireView().findViewById(R.id.bottomSheet) }
    private val bottomSheetPeeklayout: View by lazy { requireView().findViewById(R.id.linearLayoutPeek) }

    override fun getLayoutResId() = R.layout.fragment_playground_editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createBottomSheet()
        createInput()
        createCode()
        createControlButtons()

        viewModel.viewState.observe(viewLifecycleOwner, {
            if (it.code != etCode.text.toString()) {
                etCode.setText(it.code)
                etCode.setSelection(it.code.length)
            }

            if (it.inputEnabled) {
                layoutInput.visibility = View.VISIBLE
                layoutInput.requestFocus()
            } else {
                layoutInput.visibility = View.GONE
            }

            when (it.codeResponse) {
                CodeResponseModel.Loading -> {
                    showLoading(getString(R.string.executing_code))
                }
                is CodeResponseModel.Output -> showOutput(it.codeResponse.output)
                null -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                PythonEditorViewEvents.ShowUpdateCodeDialog -> showDialogToOverrideCode()
                is PythonEditorViewEvents.ShowShareIntent -> showShareIntent(it.code)
                is PythonEditorViewEvents.ShowToast -> requireActivity().toast(it.message)
                PythonEditorViewEvents.ShowFileNameDialog -> showDialogForFileName()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        // If bottom sheet is expanded, collapse it on back button
                        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

    private fun createControlButtons() {
        val controls = listOf(R.string.tab to R.string.tab_unicode,
            R.string.bracket_open to R.string.bracket_open,
            R.string.bracket_close to R.string.bracket_close,
            R.string.control_double_quotes to R.string.control_double_quotes,
            R.string.control_single_quotes to R.string.control_single_quotes,
            R.string.curly_brace_open to R.string.curly_brace_open,
            R.string.curly_brace_close to R.string.curly_brace_close,
        )

        val layoutInflater = LayoutInflater.from(context)
        controls.forEach {
            val button: MaterialButton =
                layoutInflater.inflate(R.layout.control_button, controlsContainer, false) as MaterialButton
            button.text = getString(it.first)
            button.setOnClickListener { _ ->
                if (etCode.isCursorVisible) {
                    etCode.addTextAtCursorPosition(getString(it.second))
                } else {
                    etCode.requestFocus()
                }
            }
            controlsContainer.addView(button)
        }
    }

    private fun createCode() {
        etCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hide bottom sheet when user is editing
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        requireView().findViewById<Button>(R.id.btnRun).setOnClickListener {
            etCode.clearFocus()
            viewModel.handle(PythonEditorViewActions.OnRunCode)
        }

        etCode.doAfterTextChanged {
            viewModel.handle(PythonEditorViewActions.OnCodeUpdated(it!!.toString()))
        }
    }

    private fun showDialogToOverrideCode() {
        AlertDialog.Builder(requireContext()).setMessage(getString(R.string.replace_code))
            .setPositiveButton(
                getString(android.R.string.ok)
            ) { dialog, _ ->
                dialog.dismiss()
                viewModel.handle(PythonEditorViewActions.OnOverrideCode)
            }.setNegativeButton(
                getString(android.R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
            .create().show()
    }

    private fun createBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetPeeklayout.setOnClickListener {
            // Toggle Sheet on clicking of peek layout
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
            }
        }
    }

    private fun showShareIntent(code: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, code)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_code))
        startActivity(shareIntent)

    }

    private fun showDialogForFileName() {
        val inputContainer =
            LayoutInflater.from(requireContext()).inflate(R.layout.alert_edit_text, null)
        val input = inputContainer.findViewById<EditText>(R.id.input)

        val alertDialog =
            AlertDialog.Builder(requireContext()).setMessage(getString(R.string.enter_file_name))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    viewModel.handle(PythonEditorViewActions.OnFileNameEntered(input.text.toString()))
                    dialog.dismiss()
                }
                .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.create()

        alertDialog.setView(inputContainer)
        alertDialog.show()

    }

    private fun createInput() {
        etInput.doAfterTextChanged {
            for (cs in it!!.getSpans(0, it.length, CharacterStyle::class.java)) {
                it.removeSpan(cs)
            }
        }
        ibEnter.setOnClickListener {
            etInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        // At least on API level 28, if an ACTION_UP is lost during a rotation, then the app
        // (or any other app which takes focus) will receive an endless stream of ACTION_DOWNs
        // until the key is pressed again. So we react to ACTION_UP instead.
        etInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && event.action == KeyEvent.ACTION_UP
            ) {
                viewModel.handle(PythonEditorViewActions.OnInput(etInput.text.toString()))
                etInput.setText("")
            }

            // If we return false on ACTION_DOWN, we won't be given the ACTION_UP.
            true
        }
    }

    private fun showOutput(output: CharSequence) {
        dismissLoadingDialog()
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        tvOutput.isVisible = true
        tvOutput.text = output
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                viewModel.handle(PythonEditorViewActions.ClearCode)
            }
            R.id.share -> {
                viewModel.handle(PythonEditorViewActions.ShareCode)
            }
            R.id.save -> {
                viewModel.handle(PythonEditorViewActions.OnSaveAction)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}