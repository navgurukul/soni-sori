package org.navgurukul.playground.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.playground.R
import org.navgurukul.playground.custom.addTextAtCursorPosition

class PlaygroundActivity : AppCompatActivity() {
    private val viewModel: PlaygroundViewModel by viewModel()
    private lateinit var etInput: EditText
    private lateinit var tvOutput: TextView
    private lateinit var tvError: TextView
    private lateinit var etCode: EditText
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheet: View
    private lateinit var bottomSheetTopBuffer: View
    private lateinit var bottomSheetPeeklayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        // Check of Python is started or not or else start
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playground)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        createCode()
        createBottomSheet()
        createError()
        createInput()
        createOutput()
    }


    override fun onResume() {
        viewModel.resumeStreams()
        super.onResume() // Starts the task thread.
    }

    override fun onPause() {
        super.onPause()
        if (!isChangingConfigurations) {
            viewModel.pauseStreams()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            // If home button pressed Exit the activity
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createCode() {
        etCode = findViewById(R.id.etCode)

        etCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hide bottom sheet when user is editing
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        findViewById<Button>(R.id.btnRun).setOnClickListener {
            etCode.clearFocus()
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            tvOutput.text = "" // clear output window
            viewModel.start(etCode.text.toString())
        }
    }

    private fun createError() {
        tvError = findViewById(R.id.tvError)
        viewModel.error.observe(this, Observer {
            tvError.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            tvError.text = it
        }
        )
    }

    private fun createInput() {
        etInput = findViewById(R.id.etInput)

        // Strip formatting from pasted text.
        etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(e: Editable) {
                for (cs in e.getSpans(
                    0,
                    e.length,
                    CharacterStyle::class.java
                )) {
                    e.removeSpan(cs)
                }
            }
        })

        // At least on API level 28, if an ACTION_UP is lost during a rotation, then the app
        // (or any other app which takes focus) will receive an endless stream of ACTION_DOWNs
        // until the key is pressed again. So we react to ACTION_UP instead.
        etInput.setOnEditorActionListener(OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && event.action == KeyEvent.ACTION_UP
            ) {
                val text: String = etInput.text.toString() + "\n"
                etInput.setText("")
                output(span(text, StyleSpan(Typeface.BOLD)))
                viewModel.onInput(text)
            }

            // If we return false on ACTION_DOWN, we won't be given the ACTION_UP.
            true
        })
        viewModel.inputEnabled.observe(this, Observer<Boolean?> { enabled ->
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (enabled!!) {
                etInput.visibility = View.VISIBLE
                etInput.isEnabled = true

                // requestFocus alone doesn't always bring up the soft keyboard during startup
                // on the Nexus 4 with API level 22: probably some race condition. (After
                // rotation with input *already* enabled, the focus may be overridden by
                // onRestoreInstanceState, which will run after this observer.)
                etInput.requestFocus()
                imm.showSoftInput(
                    etInput,
                    InputMethodManager.SHOW_IMPLICIT
                )
            } else {
                // Disable rather than hide, otherwise tvOutput gets a gray background on API
                // level 26, like tvCaption in the main menu when you press an arrow key.
                etInput.setEnabled(false)
                imm.hideSoftInputFromWindow(tvOutput.windowToken, 0)
            }
        })
    }

    fun span(text: CharSequence, vararg spans: Any?): Spannable {
        val spanText: Spannable = SpannableStringBuilder(text)
        for (span in spans) {
            spanText.setSpan(span, 0, text.length, 0)
        }
        return spanText
    }

    private fun createOutput() {
        tvOutput = findViewById(R.id.tvOutput)
        viewModel.output.removeObservers(this)
        viewModel.output.observe(this,
            Observer<CharSequence?> { text -> output(text!!) })
    }

    private fun createBottomSheet() {
        bottomSheet = findViewById(R.id.bottomSheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // In hidden state initially
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetPeeklayout = findViewById(R.id.linearLayoutPeek)
        bottomSheetPeeklayout.setOnClickListener {
            // Toggle Sheet on clicking of peek layout
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
            }
        }

    }


    private fun output(text: CharSequence) {
        tvOutput.append(text)
    }

    fun onClickControl(view: View) {
        if (etCode.isCursorVisible) {
            // Add text only if it is focused and cursor is visible
            when (view.id) {
                R.id.controlTab -> etCode.addTextAtCursorPosition(getString(R.string.tab_unicode))
                else -> etCode.addTextAtCursorPosition((view as TextView).text.toString())
            }
        } else {
            etCode.requestFocus()
            etCode.isCursorVisible = true
        }
    }

}