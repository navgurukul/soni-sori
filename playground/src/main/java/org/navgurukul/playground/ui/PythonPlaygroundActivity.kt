package org.navgurukul.playground.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.lifecycle.Observer
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_output.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.chat.core.utils.copyToClipboard
import org.navgurukul.playground.R
import org.navgurukul.playground.custom.addTextAtCursorPosition
import java.io.File

class PythonPlaygroundActivity : AppCompatActivity() {

    private val viewModel: PythonPlaygroundViewModel by viewModel()
    private lateinit var etInput: EditText
    private lateinit var tvOutput: TextView
    private lateinit var tvError: TextView
    private lateinit var etCode: EditText
    private lateinit var layoutInput: View
    private lateinit var ibEnter: ImageButton
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheet: View
    private lateinit var bottomSheetPeeklayout: LinearLayout
    private lateinit var errorImage: ImageView
    public lateinit var errorTextExample: TextView
    public lateinit var errorTextTip: TextView
    private lateinit var tvMentorHelp: TextView
    private lateinit var errorLayout: LinearLayout


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
        parseIntentData()
    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_CODE) && !TextUtils.isEmpty(intent.getStringExtra(ARG_CODE))) {
            val code = intent.getStringExtra(ARG_CODE)!!
            val existingCode = viewModel.getCachedCode()
            parseCodeToUI(existingCode, code)
        }

        if (intent.hasExtra(ARG_FILE_NAME)) {
            val fileName = intent.getStringExtra(ARG_FILE_NAME)!!
            val existingCode = viewModel.getCachedCode()
            val code = File(fileName).bufferedReader().readLine()
            parseCodeToUI(existingCode, code)
        }
    }

    enum class ErrorUIEnum {
        ImportError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_keyboardinterrupt)
                exampleText.setText(context.getString(R.string.importErrorExampleText))
                tipText.setText(context.getString(R.string.importErrorTiptext))
            }
        },
        ModuleNotFoundError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_modulenotfound)
                exampleText.setText(context.getString(R.string.moduleNotFoundExampleText))
                tipText.setText(context.getString(R.string.moduleNotFoundeTipText))
            }
        },
        IndexError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_indexerror)
                exampleText.setText(context.getString(R.string.indexErrorExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        StopIteration {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_stopiteration)
                exampleText.setText(context.getString(R.string.stopIterationExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        ValueError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_valueerror)
                exampleText.setText(context.getString(R.string.valueErrorExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        TypeError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_typeerror)
                exampleText.setText(context.getString(R.string.typeErrorExampleText))
                tipText.setText(context.getString(R.string.typeErrorTipText))
            }
        },
        NameError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_nameerror)
                exampleText.setText(context.getString(R.string.nameErrorExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        KeyError {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_keyerror)
                exampleText.setText(context.getString(R.string.keyErrorExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        KeyboardInterrupt {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_keyboardinterrupt)
                exampleText.setText(context.getString(R.string.keyboardInterruptExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        },
        default {
            override fun apply(
                context: Context,
                errorImage: ImageView,
                exampleText: TextView,
                tipText: TextView
            ) {
                errorImage.setImageResource(R.drawable.ic_keyboardinterrupt)
                exampleText.setText(context.getString(R.string.keyboardInterruptExampleText))
                tipText.setText(context.getString(R.string.tipText))
            }
        };

        abstract fun apply(
            context: Context,
            errorImage: ImageView,
            exampleText: TextView,
            tipText: TextView
        )
    }


    private fun parseCodeToUI(existingCode: String, code: String) {
        if (TextUtils.isEmpty(existingCode)) {
            etCode.setText(code)
            etCode.setSelection(etCode.text.length)
        } else {
            showDialogToOverrideCode(code)
        }
    }

    private fun showDialogToOverrideCode(code: String) {
        AlertDialog.Builder(this).setMessage(getString(R.string.replace_code))
            .setPositiveButton(
                getString(android.R.string.ok)
            ) { dialog, _ ->
                dialog.dismiss()
                etCode.setText(code)
                etCode.setSelection(etCode.text.length)
            }.setNegativeButton(
                getString(android.R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
            .create().show()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.playground_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            // If home button pressed Exit the activity
            super.onBackPressed()
            return true
        } else if (id == R.id.clear) {
            etCode.text.clear()
        } else if (id == R.id.share) {
            shareCode()
        } else if (id == R.id.save) {
            saveCode()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareCode() {
        if (!TextUtils.isEmpty(etCode.text.toString())) {
            showShareIntent(etCode.text.toString())
        } else {
            Toast.makeText(
                this@PythonPlaygroundActivity,
                getString(R.string.nothing_to_share),
                Toast.LENGTH_SHORT
            ).show()
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

    private fun saveCode() {
        if (!TextUtils.isEmpty(etCode.text.toString())) {
            showDialogForFileName()
        } else {
            Toast.makeText(
                this@PythonPlaygroundActivity,
                getString(R.string.nothing_to_save),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialogForFileName() {
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(10, 10, 10, 10)
        input.layoutParams = lp

        val alertDialog = AlertDialog.Builder(this).setMessage(getString(R.string.enter_file_name))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.saveCode(etCode.text.toString(), input.text.toString())
                dialog.dismiss()
                Toast.makeText(
                    this@PythonPlaygroundActivity,
                    getString(R.string.code_saved),
                    Toast.LENGTH_SHORT
                ).show()
            }.create()

        alertDialog.setView(input)
        alertDialog.show()

    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            // If bottom sheet is expanded, collapse it on back button
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        super.onBackPressed()
    }

    private fun createCode() {
        etCode = findViewById(R.id.etCode)

        // Restore code from shared Prefs
        etCode.setText(viewModel.getCachedCode())
        etCode.setSelection(etCode.text.length)

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

        etCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // cache text to shared Pref
                viewModel.cacheCode(etCode.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do Nothing
            }

        }

        )
    }

    private fun createError() {
        tvError = findViewById(R.id.tvError)
        viewModel.error.observe(this, Observer {
            tvError.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            tvError.text = it

            createErrorUI()
        }
        )
    }

    private fun createInput() {
        layoutInput = findViewById(R.id.layoutInput)
        etInput = findViewById(R.id.etInput)
        ibEnter = findViewById(R.id.ibEnter)
        // Strip formatting from pasted text.
        etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do nothing
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                // Do nothing
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
        ibEnter.setOnClickListener {
            etInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        // At least on API level 28, if an ACTION_UP is lost during a rotation, then the app
        // (or any other app which takes focus) will receive an endless stream of ACTION_DOWNs
        // until the key is pressed again. So we react to ACTION_UP instead.
        etInput.setOnEditorActionListener(OnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && event.action == KeyEvent.ACTION_UP
            ) {
                // Add explicit space from the input
                val text: String = " " + etInput.text.toString() + "\n"
                etInput.setText("")
                output(span(text, StyleSpan(Typeface.BOLD)))
                viewModel.onInput(text)
                svOutput.post {
                    svOutput.fullScroll(View.FOCUS_DOWN)
                }
            }

            // If we return false on ACTION_DOWN, we won't be given the ACTION_UP.
            true
        })
        viewModel.inputEnabled.observe(this, Observer<Boolean> { enabled ->
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (enabled) {
                layoutInput.visibility = View.VISIBLE
                ibEnter.isEnabled = true
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
                layoutInput.visibility = View.GONE
                ibEnter.isEnabled = false
                etInput.isEnabled = false
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

    private fun createErrorUI() {
        errorImage = findViewById(R.id.errorImage)
        errorTextExample = findViewById(R.id.errorTextExample)
        errorTextTip = findViewById(R.id.errorTextTip)
        tvMentorHelp = findViewById(R.id.tvMentorHelp)
        errorLayout = findViewById(R.id.errorLayout)

        findViewById<TextView>(R.id.copy_btn).setOnClickListener {
            copyToClipboard(this, tvError.text.toString())
            Toast.makeText(this, "Copied !!", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.cancel_btn).setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            onBackPressed()
        }

        val errorText = tvError.text.toString()

        when {
            errorText.matches(Regex("^.*ImportError:([a-zA-Z]+( [a-zA-Z]+)+).*'.*'\$")) -> { // 1
                ErrorUIEnum.ImportError.apply(this, errorImage, errorTextExample, errorTextTip)
            }
            errorText.matches(Regex(" ^.*ModuleNotFoundError+.*:.*([a-zA-Z]+( [a-zA-Z]+)+).*'.*'\$")) -> { //2
                ErrorUIEnum.ModuleNotFoundError.apply(
                    this,
                    errorImage,
                    errorTextExample,
                    errorTextTip
                )
            }
            errorText.matches(Regex("^.*IndexError+.*:.*([a-zA-Z]+( [a-zA-Z]+)+).*\$")) -> { //3
                ErrorUIEnum.IndexError.apply(this, errorImage, errorTextExample, errorTextTip)
            }
            errorText.matches(Regex("^.*StopIteration+.*:.*([a-zA-Z]+( [a-zA-Z]+)+).*\$")) -> {//4
                ErrorUIEnum.StopIteration.apply(this, errorImage, errorTextExample, errorTextTip);
            }
            errorText.matches(Regex("^.*ValueError+.*:.*([a-zA-Z]+( [a-zA-Z]+)+).*\$")) -> {//5
                ErrorUIEnum.ValueError.apply(this, errorImage, errorTextExample, errorTextTip)
            }
            errorText.matches(Regex("^.*TypeError*r.*([a-zA-Z]+( [a-zA-Z]+)+).*\$")) -> {//6
                ErrorUIEnum.TypeError.apply(this, errorImage, errorTextExample, errorTextTip)
            }
            errorText.matches(Regex("^.*NameError+.*:.*[a-zA-Z]+.*'.*'.*([a-zA-Z]+( [a-zA-Z]+)+)")) -> {//7
                ErrorUIEnum.NameError.apply(this, errorImage, errorTextExample, errorTextTip);
            }
            errorText.matches(Regex("^.*KeyError+.*:.*\$")) -> {//8  //^.*KeyError+.*:.*$
                ErrorUIEnum.KeyError.apply(this, errorImage, errorTextExample, errorTextTip)
            }
            errorText.matches(Regex("^.*[a-zA-Z]+.*:.*KeyboardInterrupt+\$")) -> {//9
                ErrorUIEnum.KeyboardInterrupt.apply(
                    this,
                    errorImage,
                    errorTextExample,
                    errorTextTip
                )
            }
            else -> {
                ErrorUIEnum.default.apply(this, errorImage, errorTextExample, errorTextTip)
            }
        }

    }


    fun toFindRegexMatch(pattern: String, errorStackTrace: String) {


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

    companion object {
        private const val ARG_CODE = "arg_code"
        private const val ARG_FILE_NAME = "arg_file_name"
        fun launch(code: String?, context: Context): Intent {
            val intent = Intent(context, PythonPlaygroundActivity::class.java)
            intent.putExtra(ARG_CODE, code)
            return intent
        }

        fun launchWithFileContent(fileName: String, context: Context): Intent {
            val intent = Intent(context, PythonPlaygroundActivity::class.java)
            intent.putExtra(ARG_FILE_NAME, fileName)
            return intent
        }
    }

}
