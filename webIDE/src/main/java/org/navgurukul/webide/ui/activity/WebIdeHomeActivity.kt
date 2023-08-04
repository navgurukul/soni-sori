package org.navgurukul.webide.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.merakilearn.util.webide.ROOT_PATH
import org.navgurukul.webide.R
import org.navgurukul.webide.databinding.ActivityWebIdeHomeBinding
import org.navgurukul.webide.databinding.DialogCloneBinding
import org.navgurukul.webide.databinding.DialogCreate2Binding
import org.navgurukul.webide.databinding.DialogImportBinding
import org.navgurukul.webide.extensions.intentFor
import org.navgurukul.webide.extensions.startAndFinish
import org.navgurukul.webide.git.GitWrapper
import org.navgurukul.webide.ui.adapter.ProjectAdapter
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.Prefs.set
import org.navgurukul.webide.util.editor.ResourceHelper
import org.navgurukul.webide.util.project.DataValidator
import org.navgurukul.webide.util.project.ProjectManager
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.util.*

//@Parcelize
//data class WebIdeHomeActivityArgs(
//    val mode: Mode,
//    val retake: Boolean = false
//) : Parcelable

class WebIdeHomeActivity : ThemedActivity(), SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

    private var contents: Array<String>? = null
    private var contentsList: ArrayList<String>? = null
    private lateinit var projectAdapter: ProjectAdapter

    private var imageStream: InputStream? = null
    private lateinit var projectIcon: ImageView
    private lateinit var prefs: SharedPreferences

    private lateinit var binding: ActivityWebIdeHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebIdeHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.include.toolbar)

        prefs = defaultPrefs(this)
        contents = File(this.ROOT_PATH()).list { dir, name ->
            dir.isDirectory && name != ".git" && ProjectManager.isValid(
                this,
                name
            )
        }
        contentsList = if (contents != null) {
            ArrayList(Arrays.asList(*contents!!))
        } else {
            ArrayList()
        }

        DataValidator.removeBroken(this, contentsList!!)
        projectAdapter =
            ProjectAdapter(this, contentsList!!, binding.coordinatorLayout, binding.projectList)
        val layoutManager = LinearLayoutManager(this)
        binding.projectList.layoutManager = layoutManager
        binding.projectList.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            )
        )
        binding.projectList.itemAnimator = DefaultItemAnimator()
        binding.projectList.adapter = projectAdapter
        binding.cloneButton.setOnClickListener {
            val choices =
                arrayOf("Create a new project", "Clone a repository", "Import an external project")
            AlertDialog.Builder(this@WebIdeHomeActivity)
                .setTitle("Would you like to...")
                .setAdapter(
                    ArrayAdapter(
                        this@WebIdeHomeActivity,
                        android.R.layout.simple_list_item_1,
                        choices
                    )
                ) { _, i ->
                    when (i) {
                        0 -> {
                            val rootView =
                                DialogCreate2Binding.inflate(LayoutInflater.from(this@WebIdeHomeActivity))
                            rootView.typeSpinner.adapter = ArrayAdapter(
                                this@WebIdeHomeActivity,
                                android.R.layout.simple_list_item_1,
                                ProjectManager.TYPES
                            )
                            rootView.typeSpinner.setSelection(prefs["type", 0]!!)
                            rootView.nameLayout.editText!!.setText(prefs["name", ""])
                            rootView.authorLayout.editText!!.setText(prefs["author", ""])
                            rootView.descLayout.editText!!.setText(prefs["description", ""])
                            rootView.keyLayout.editText!!.setText(prefs["keywords", ""])

                            projectIcon = rootView.faviconImage
                            rootView.defaultIcon.isChecked = true
                            rootView.defaultIcon.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    projectIcon.setImageResource(R.drawable.ic_launcher)
                                    imageStream = null
                                }
                            }

                            rootView.chooseIcon.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                                    intent.type = "image/*"
                                    startActivityForResult(intent, SELECT_ICON)
                                }
                            }

                            val createDialog = AlertDialog.Builder(this@WebIdeHomeActivity)
                                .setTitle("Create a new project")
                                .setView(rootView.root)
                                .setPositiveButton("CREATE", null)
                                .setNegativeButton("CANCEL", null)
                                .create()

                            createDialog.show()
                            createDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                if (DataValidator.validateCreate(
                                        this@WebIdeHomeActivity,
                                        rootView.nameLayout,
                                        rootView.authorLayout,
                                        rootView.descLayout,
                                        rootView.keyLayout
                                    )
                                ) {
                                    val name = rootView.nameLayout.editText!!.text.toString()
                                    val author = rootView.authorLayout.editText!!.text.toString()
                                    val description = rootView.descLayout.editText!!.text.toString()
                                    val keywords = rootView.keyLayout.editText!!.text.toString()
                                    val type = rootView.typeSpinner.selectedItemPosition

                                    prefs["name"] = name
                                    prefs["author"] = author
                                    prefs["description"] = description
                                    prefs["keywords"] = keywords
                                    prefs["type"] = type

                                    ProjectManager.generate(
                                        this@WebIdeHomeActivity,
                                        name,
                                        author,
                                        description,
                                        keywords,
                                        imageStream,
                                        projectAdapter,
                                        binding.coordinatorLayout,
                                        type
                                    )

                                    createDialog.dismiss()
                                }
                            }
                        }
                        1 -> {
                            val cloneView =
                                DialogCloneBinding.inflate(LayoutInflater.from(this@WebIdeHomeActivity))
                            cloneView.cloneName.setText(prefs["clone_name", ""])
                            cloneView.cloneUrl.setText(prefs["remote", ""])
                            val cloneDialog = AlertDialog.Builder(this@WebIdeHomeActivity)
                                .setTitle("Clone a repository")
                                .setView(cloneView.root)
                                .setPositiveButton("CLONE", null)
                                .setNegativeButton(R.string.cancel, null)
                                .create()

                            cloneDialog.show()
                            cloneDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                                if (DataValidator.validateClone(
                                        this@WebIdeHomeActivity,
                                        cloneView.cloneName,
                                        cloneView.cloneUrl
                                    )
                                ) {
                                    var remoteStr = cloneView.cloneUrl.text.toString()
                                    if (!remoteStr.contains("://")) {
                                        remoteStr = "https://$remoteStr"
                                    }

                                    val cloneName = cloneView.cloneName.text.toString()
                                    prefs["clone_name"] = cloneName
                                    prefs["remote"] = remoteStr

                                    GitWrapper.clone(
                                        this@WebIdeHomeActivity,
                                        binding.coordinatorLayout,
                                        File(this.ROOT_PATH() + File.separator + cloneName),
                                        projectAdapter,
                                        remoteStr,
                                        cloneView.cloneUsername.text.toString(),
                                        cloneView.clonePassword.text.toString()
                                    )

                                    cloneDialog.dismiss()
                                }
                            }
                        }
                        2 -> {
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "file/*"
                            intent.resolveActivity(packageManager)?.let {
                                startActivityForResult(intent, IMPORT_PROJECT)
                            }
                        }
                    }
                }
                .show()
        }

        binding.projectList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.cloneButton.show()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.cloneButton.isShown) binding.cloneButton.hide()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //   R.id.action_settings -> startActivityForResult<SettingsActivity>(SETTINGS_CODE)
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_ICON -> if (resultCode == Activity.RESULT_OK) {
                try {
                    val selectedImage = data!!.data
                    selectedImage?.let {
                        imageStream =
                            this@WebIdeHomeActivity.contentResolver.openInputStream(selectedImage)
                        projectIcon.setImageBitmap(
                            ResourceHelper.decodeUri(
                                this@WebIdeHomeActivity,
                                selectedImage
                            )
                        )
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }

            SETTINGS_CODE -> startAndFinish(intentFor<WebIdeHomeActivity>())

            IMPORT_PROJECT -> if (resultCode == Activity.RESULT_OK) {
                val fileUri = data!!.data!!
                val file = File(fileUri.path)
                val rootView =
                    DialogImportBinding.inflate(LayoutInflater.from(this@WebIdeHomeActivity))
                rootView.impTypeSpinner.adapter = ArrayAdapter(
                    this@WebIdeHomeActivity,
                    android.R.layout.simple_list_item_1,
                    ProjectManager.TYPES
                )
                rootView.impTypeSpinner.setSelection(prefs["type", 0]!!)

                rootView.impNameLayout.editText!!.setText(file.parentFile.name)
                rootView.impAuthorLayout.editText!!.setText(prefs["author", ""])
                rootView.impDescLayout.editText!!.setText(prefs["description", ""])
                rootView.impKeyLayout.editText!!.setText(prefs["keywords", ""])

                val createDialog = AlertDialog.Builder(this@WebIdeHomeActivity)
                    .setTitle("Import an external project")
                    .setIcon(R.drawable.ic_action_import)
                    .setView(rootView.root)
                    .setPositiveButton("IMPORT", null)
                    .setNegativeButton("CANCEL", null)
                    .create()

                createDialog.show()
                createDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (DataValidator.validateCreate(
                            this@WebIdeHomeActivity,
                            rootView.impNameLayout,
                            rootView.impAuthorLayout,
                            rootView.impDescLayout,
                            rootView.impKeyLayout
                        )
                    ) {
                        val name = rootView.impNameLayout.editText!!.text.toString()
                        val author = rootView.impAuthorLayout.editText!!.text.toString()
                        val description = rootView.impDescLayout.editText!!.text.toString()
                        val keywords = rootView.impKeyLayout.editText!!.text.toString()
                        val type = rootView.impTypeSpinner.selectedItemPosition

                        prefs["name"] = name
                        prefs["author"] = author
                        prefs["description"] = description
                        prefs["keywords"] = keywords
                        prefs["type"] = type

                        ProjectManager.importProject(
                            this,
                            file.parentFile.path,
                            name,
                            author,
                            description,
                            keywords,
                            projectAdapter,
                            binding.coordinatorLayout
                        )

                        createDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean = false

    override fun onQueryTextChange(newText: String): Boolean {
        contentsList = ArrayList(Arrays.asList(*contents!!))
        DataValidator.removeBroken(this, contentsList!!)
        val iterator = contentsList!!.iterator()
        while (iterator.hasNext()) {
            if (!iterator.next().toLowerCase(Locale.getDefault()).contains(newText)) {
                iterator.remove()
            }
        }

        projectAdapter = ProjectAdapter(
            this@WebIdeHomeActivity,
            contentsList!!,
            binding.coordinatorLayout,
            binding.projectList
        )
        binding.projectList.adapter = projectAdapter
        return true
    }

    override fun onClose(): Boolean {
        contentsList = ArrayList(Arrays.asList(*contents!!))
        DataValidator.removeBroken(this, contentsList!!)
        projectAdapter = ProjectAdapter(
            this@WebIdeHomeActivity,
            contentsList!!,
            binding.coordinatorLayout,
            binding.projectList
        )
        binding.projectList.adapter = projectAdapter
        return false
    }

    companion object {

        private const val SELECT_ICON = 100
        private const val SETTINGS_CODE = 101
        private const val IMPORT_PROJECT = 102

//        fun newIntent(context: Context, mode: Mode, retake: Boolean = false): Intent {
//            return Intent(context, WebIdeHomeActivity::class.java).apply {
//                putExtras(WebIdeHomeActivityArgs(mode, retake).toBundle()!!)
//            }
//        }
    }
}