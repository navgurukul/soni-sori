package org.navgurukul.playground.editor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.playground.R
import java.io.File

class PythonEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playground)
        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_view, PythonEditorFragment().apply {
                    arguments = intent.extras
                })
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.python_playground_menu, menu)
        menu?.let{
            for (i in 0 until it.size()) {
                val menuItem = it.getItem(i)
                val spannable = SpannableString(
                    it.getItem(i).title.toString()
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0, spannable.length, 0
                )
                menuItem.title = spannable
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // If home button pressed Exit the activity
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EMPTY_FILE:String="Untitled"
        fun launch(code: String?, context: Context): Intent {
            val intent = Intent(context, PythonEditorActivity::class.java)
            intent.putExtras(PythonEditorArgs(code,File(EMPTY_FILE),true).toBundle()!!)
            return intent
        }

        fun launchWithFileContent(file: File, context: Context): Intent {
            val intent = Intent(context, PythonEditorActivity::class.java)
            val code = file.bufferedReader().readText()
            intent.putExtras(PythonEditorArgs(code,file,false).toBundle()!!)
            return intent
        }
    }

}

@Parcelize
data class PythonEditorArgs(
    val code: String?,
    val file: File,
    val newFile:Boolean,
) : Parcelable
