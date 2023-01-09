package org.navgurukul.webide

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.widget_toolbar.*
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.navgurukul.webIDE.R
import org.navgurukul.webide.ui.adapter.ProjectAdapter
import org.navgurukul.webide.util.Constants
import org.navgurukul.webide.util.Prefs
import org.navgurukul.webide.util.project.ProjectManager
import java.io.File
import java.io.InputStream
import java.util.*

@Parcelize
data class MainActivityArgs(
    val mode: Mode,
    val retake: Boolean = false
) : Parcelable

class MainActivity : AppCompatActivity() {

    private var contents: Array<String>? = null
    private var contentsList: ArrayList<String>? = null
    private lateinit var projectAdapter: ProjectAdapter

    private var imageStream: InputStream? = null
    private lateinit var projectIcon: ImageView
    private lateinit var prefs: SharedPreferences

    companion object {

        private const val SELECT_ICON = 100
        private const val SETTINGS_CODE = 101
        private const val IMPORT_PROJECT = 102

        fun newIntent(context: Context, mode: Mode, retake: Boolean = false): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtras(MainActivityArgs(mode, retake).toBundle()!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = Prefs.defaultPrefs(this)
        contents = File(Constants.HYPER_ROOT).list { dir, name -> dir.isDirectory && name != ".git" && ProjectManager.isValid(name) }
        contentsList = if (contents != null) {
            ArrayList(Arrays.asList(*contents!!))
        } else {
            ArrayList()
        }
    }
}