package org.navgurukul.webide.ui.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.webIDE.R
import org.navgurukul.webIDE.databinding.*
import org.navgurukul.webide.extensions.*
import org.navgurukul.webide.git.GitWrapper
import org.navgurukul.webide.ui.adapter.FileAdapter
import org.navgurukul.webide.ui.adapter.FileBrowserAdapter
import org.navgurukul.webide.ui.adapter.GitLogsAdapter
import org.navgurukul.webide.ui.fragment.EditorFragment
import org.navgurukul.webide.ui.fragment.ImageFragment
import org.navgurukul.webide.ui.helper.MenuPrepareHelper
import org.navgurukul.webide.ui.viewmodel.ProjectViewModel
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.ROOT_PATH
import org.navgurukul.webide.util.net.HtmlParser
import org.navgurukul.webide.util.project.ProjectManager
import org.navgurukul.webide.util.ui.Styles
import java.io.File

class ProjectActivity : BaseActivity() {

    private lateinit var projectViewModel: ProjectViewModel

    private lateinit var fileSpinner: Spinner
    private lateinit var fileAdapter: FileAdapter

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var projectName: String
    private lateinit var projectDir: File
    private lateinit var indexFile: File
    private lateinit var props: Array<String?>
    private lateinit var prefs: SharedPreferences

    private lateinit var adapter: FileBrowserAdapter

    private lateinit var binding: ActivityProjectBinding
    override fun shouldInstallDynamicModule() = true
    override fun onCreate(savedInstanceState: Bundle?) {
        projectName = intent.getStringExtra("project")!!
        projectDir = File("${this.ROOT_PATH()}/$projectName")
        indexFile = ProjectManager.getIndexFile(this, projectName)!!
        setTheme(Styles.getThemeInt(this))
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = defaultPrefs(this)
        props = HtmlParser.getProperties(this, projectName)
        fileAdapter = FileAdapter(this, ArrayList())

        projectViewModel = ViewModelProviders.of(this).get(ProjectViewModel::class.java)
        projectViewModel.openFiles.observe(this, Observer { fileAdapter.update(it) })
        projectViewModel.openFiles.value = if (intent.hasExtra("files")) {
            intent.getStringArrayListExtra("files")
        } else {
            arrayListOf(indexFile.path)
        }

        fileSpinner = Spinner(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adapter = fileAdapter
            onItemSelected {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.editorFragment,
                        getFragment(projectViewModel.openFiles.value!![it])
                    )
                    .commit()
            }
        }

        binding.include.toolbar.addView(fileSpinner)
        setSupportActionBar(binding.include.toolbar)
        supportActionBar!!.title = ""

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.include.toolbar,
            R.string.action_drawer_open,
            R.string.action_drawer_close
        )
        with(binding.drawerLayout) {
            addDrawerListener(toggle)
            setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
            setStatusBarBackgroundColor(this@ProjectActivity.compatColor(R.color.colorPrimaryDark))
            onDrawerOpened {
                props = HtmlParser.getProperties(this@ProjectActivity, projectName)
                binding.headerTitle.text = props[0]
                binding.headerDesc.text = props[1]
            }
        }

        binding.fileBrowser.layoutManager = LinearLayoutManager(this)
        binding.fileBrowser.itemAnimator = DefaultItemAnimator()
        adapter = FileBrowserAdapter(this, projectName, binding.drawerLayout, projectViewModel) {
            if (it.isFile) {
                if (projectViewModel.openFiles.value!!.contains(it.path)) {
                    setFragment(it.path, false)
                    binding.drawerLayout.closeDrawers()
                } else {
                    if (!ProjectManager.isBinaryFile(it) || ProjectManager.isImageFile(it)) {
                        setFragment(it.path, true)
                        binding.drawerLayout.closeDrawers()
                    } else {
                        binding.drawerLayout.snack(R.string.not_text_file)
                    }
                }
            }
        }

        binding.fileBrowser.adapter = adapter

        binding.headerIcon.setImageBitmap(ProjectManager.getFavicon(this, projectName))
        binding.headerTitle.text = props[0]
        binding.headerDesc.text = props[1]

        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = 0x00000000
            val description = ActivityManager.TaskDescription(
                projectName,
                ProjectManager.getFavicon(this, projectName)
            )
            this.setTaskDescription(description)
        }
    }

    private fun setFragment(filePath: String, add: Boolean) {
        if (add) projectViewModel.addOpenFile(filePath)

        fileSpinner.setSelection(fileAdapter.getPosition(filePath), true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.editorFragment, getFragment(filePath))
            .commit()
    }

    private fun getFragment(filePath: String): Fragment {
        val bundle = Bundle().apply {
            putInt("position", fileAdapter.count)
            putString("location", filePath)
        }

        return if (ProjectManager.isImageFile(File(filePath))) {
            ImageFragment.newInstance(bundle)
        } else {
            EditorFragment.newInstance(bundle)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isGitRepo = File(projectDir, ".git").exists() && File(projectDir, ".git").isDirectory
        val params = arrayOf(
            (fileSpinner.selectedItem as String).endsWith(".html"),
            isGitRepo,
            false,
            false,
            false
        )
        if (isGitRepo) {
            params[2] = GitWrapper.canCommit(binding.drawerLayout, projectDir)
            params[3] = GitWrapper.getRemotes(binding.drawerLayout, projectDir) != null &&
                    GitWrapper.getRemotes(binding.drawerLayout, projectDir)!!.size > 0
            params[4] = GitWrapper.canCheckout(binding.drawerLayout, projectDir)
        }

        return MenuPrepareHelper.prepare(menu, *params.toBooleanArray())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_run -> startActivity<WebActivity>(
                "url" to "file:///${indexFile.path}",
                "name" to projectName
            )

            R.id.action_import_file -> with(Intent(Intent.ACTION_GET_CONTENT)) {
                type = "file/*"
                resolveActivity(packageManager)?.let {
                    startActivityForResult(this, IMPORT_FILE)
                }
            }

            R.id.action_about -> showAbout()
            R.id.action_git_init -> GitWrapper.init(this, projectDir, binding.drawerLayout)
            R.id.action_git_add -> GitWrapper.add(binding.drawerLayout, projectDir)
            R.id.action_git_commit -> {
                val view =
                    DialogInputSingleBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                view.inputText.setHint(R.string.commit_message)

                val commitDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.git_commit)
                    .setView(view.root)
                    .setCancelable(false)
                    .setPositiveButton(R.string.git_commit, null)
                    .setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.cancel() }
                    .create()

                commitDialog.show()
                commitDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (!view.inputText.string().isEmpty()) {
                        GitWrapper.commit(
                            this,
                            binding.drawerLayout,
                            projectDir,
                            view.inputText.string()
                        )
                        commitDialog.dismiss()
                    } else {
                        view.inputText.error = getString(R.string.commit_message_empty)
                    }
                }
            }

            R.id.action_git_push -> {
                val pushView = DialogPushBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                pushView.pushSpinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    GitWrapper.getRemotes(binding.drawerLayout, projectDir)!!
                )
                AlertDialog.Builder(this)
                    .setTitle("Push changes")
                    .setView(pushView.root)
                    .setPositiveButton("PUSH") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        GitWrapper.push(
                            this,
                            binding.drawerLayout,
                            projectDir,
                            pushView.pushSpinner.selectedItem as String,
                            booleanArrayOf(
                                pushView.dryRun.isChecked,
                                pushView.force.isChecked,
                                pushView.thin.isChecked,
                                pushView.tags.isChecked
                            ),
                            pushView.pushUsername.string(),
                            pushView.pushPassword.string()
                        )
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            R.id.action_git_pull -> {
                val pullView = DialogPullBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                pullView.remotesSpinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    GitWrapper.getRemotes(binding.drawerLayout, projectDir)!!
                )
                AlertDialog.Builder(this)
                    .setTitle("Push changes")
                    .setView(pullView.root)
                    .setPositiveButton("PULL") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        GitWrapper.pull(
                            this,
                            binding.drawerLayout,
                            projectDir,
                            pullView.remotesSpinner.selectedItem as String,
                            pullView.pullUsername.string(),
                            pullView.pullPassword.string()
                        )
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            R.id.action_git_log -> {
                val commits = GitWrapper.getCommits(binding.drawerLayout, projectDir)
                val layoutLog = SheetLogsBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                if (prefs["dark_theme", false]!!) {
                    layoutLog.root.setBackgroundColor(-0xcccccd)
                }

                val manager = LinearLayoutManager(this)
                val adapter = GitLogsAdapter(commits)

                layoutLog.logsList.layoutManager = manager
                layoutLog.logsList.adapter = adapter

                val dialogLog = BottomSheetDialog(this)
                dialogLog.setContentView(layoutLog.root)
                dialogLog.show()
            }

            R.id.action_git_diff -> {
                val chosen = intArrayOf(-1, -1)
                val commitsToDiff = GitWrapper.getCommits(binding.drawerLayout, projectDir)
                val commitNames = arrayOfNulls<CharSequence>(commitsToDiff!!.size)
                for (i in commitNames.indices) {
                    commitNames[i] = commitsToDiff[i].shortMessage
                }

                AlertDialog.Builder(this)
                    .setTitle("Choose first commit")
                    .setSingleChoiceItems(commitNames, -1) { dialogInterface, i ->
                        dialogInterface.cancel()
                        chosen[0] = i
                        AlertDialog.Builder(this)
                            .setTitle("Choose second commit")
                            .setSingleChoiceItems(commitNames, -1) { dialogIface, i2 ->
                                dialogIface.cancel()
                                chosen[1] = i2
                                val string = GitWrapper.diff(
                                    binding.drawerLayout,
                                    projectDir,
                                    commitsToDiff[chosen[0]].id,
                                    commitsToDiff[chosen[1]].id
                                )
                                val rootView =
                                    DialogDiffBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                                rootView.diffView.setDiffText(string!!)

                                AlertDialog.Builder(this)
                                    .setView(rootView.root)
                                    .show()
                            }
                            .show()
                    }
                    .show()
            }

            R.id.action_git_status -> {
                val status = ItemGitStatusBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                View.inflate(this, R.layout.item_git_status, null)
                if (prefs["dark_theme", false]!!) {
                    status.root.setBackgroundColor(-0xcccccd)
                }

                GitWrapper.status(
                    binding.drawerLayout,
                    projectDir,
                    status.conflict,
                    status.added,
                    status.changed,
                    status.missing,
                    status.modified,
                    status.removed,
                    status.uncommitted,
                    status.untracked,
                    status.untrackedFolders
                )
                val dialogStatus = BottomSheetDialog(this)
                dialogStatus.setContentView(status.root)
                dialogStatus.show()
            }

            R.id.action_git_branch_new -> {
                val branchView =
                    DialogGitBranchBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                branchView.checkout.setText(R.string.checkout)

                val branchDialog = AlertDialog.Builder(this)
                    .setTitle("New branch")
                    .setView(branchView.root)
                    .setPositiveButton(R.string.create, null)
                    .setNegativeButton(R.string.cancel, null)
                    .create()

                branchDialog.show()
                branchDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (!branchView.branchName.string().isEmpty()) {
                        GitWrapper.createBranch(
                            this,
                            binding.drawerLayout,
                            projectDir,
                            branchView.branchName.string(),
                            branchView.checkout.isChecked
                        )
                        branchDialog.dismiss()
                    } else {
                        branchView.branchName.error = getString(R.string.branch_name_empty)
                    }
                }
            }

            R.id.action_git_branch_remove -> {
                val branchesList = GitWrapper.getBranches(binding.drawerLayout, projectDir)
                val itemsMultiple = arrayOfNulls<CharSequence>(branchesList!!.size)
                for (i in itemsMultiple.indices) {
                    itemsMultiple[i] = branchesList[i].name
                }

                val checkedItems = BooleanArray(itemsMultiple.size)
                val toDelete = ArrayList<String>()

                AlertDialog.Builder(this)
                    .setMultiChoiceItems(itemsMultiple, checkedItems) { _, i, b ->
                        if (b) {
                            toDelete.add(itemsMultiple[i].toString())
                        } else {
                            toDelete.remove(itemsMultiple[i].toString())
                        }
                    }
                    .setPositiveButton(R.string.delete) { dialogInterface, _ ->
                        GitWrapper.deleteBranch(
                            binding.drawerLayout,
                            projectDir,
                            *toDelete.toTypedArray()
                        )
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton(R.string.close, null)
                    .setTitle("Delete branches")
                    .show()
            }

            R.id.action_git_branch_checkout -> {
                val branches = GitWrapper.getBranches(binding.drawerLayout, projectDir)
                var checkedItem = -1
                val items = arrayOfNulls<CharSequence>(branches!!.size)
                for (i in items.indices) {
                    items[i] = branches[i].name
                }

                for (i in items.indices) {
                    val branch = GitWrapper.getCurrentBranch(binding.drawerLayout, projectDir)
                    branch?.let {
                        if (branch == items[i]) {
                            checkedItem = i
                        }
                    }
                }

                AlertDialog.Builder(this)
                    .setSingleChoiceItems(items, checkedItem) { dialogInterface, i ->
                        dialogInterface.dismiss()
                        GitWrapper.checkout(
                            this,
                            binding.drawerLayout,
                            projectDir,
                            branches[i].name
                        )
                    }
                    .setNegativeButton(R.string.close, null)
                    .setTitle("Checkout branch")
                    .show()
            }

            R.id.action_git_remote -> startActivity<RemotesActivity>("project_file" to projectDir.path)
            R.id.action_analyze -> startActivity<AnalyzeActivity>("project_file" to projectDir.path)

            else -> return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMPORT_FILE -> if (resultCode == Activity.RESULT_OK) {
                val fileUri = data!!.data
                val view =
                    DialogInputSingleBinding.inflate(LayoutInflater.from(this@ProjectActivity))
                view.inputText.setHint(R.string.file_name)

                val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.name)
                    .setView(view.root)
                    .setCancelable(false)
                    .setPositiveButton(R.string.import_not_java, null)
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .create()

                dialog.show()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    if (view.inputText.string().isEmpty()) {
                        view.inputText.error = "Please enter a name"
                    } else {
                        dialog.dismiss()
                        if (ProjectManager.importFile(
                                this,
                                projectName,
                                fileUri!!,
                                view.inputText.string()
                            )
                        ) {
                            binding.drawerLayout.snack(R.string.file_success, Snackbar.LENGTH_SHORT)
                        } else {
                            binding.drawerLayout.snack(R.string.file_fail)
                        }
                    }
                }
            }
        }

        adapter.updateFiles()
    }

    private fun showAbout() {
        props = HtmlParser.getProperties(this, projectName)
        with(BottomSheetDialog(this)) {
            val sheetAboutBinding =
                SheetAboutBinding.inflate(LayoutInflater.from(this@ProjectActivity))
            setContentView(sheetAboutBinding.root)
            sheetAboutBinding.apply {
                projName.text = props[0]
                projAuthor.text = props[1]
                projDesc.text = props[2]
                projKey.text = props[3]

                if (prefs["dark_theme", false]!!) {
                    root.setBackgroundColor(-0xcccccd)
                }
            }
            show()
        }
    }

    companion object {
        fun newIntent(context: Context, projectName: String): Intent {
            return Intent(context, ProjectActivity::class.java).apply {
                putExtra("project", projectName)
                // putExtras(WebIdeArgs(projectName).toBundle()!!)
            }
        }

        private const val IMPORT_FILE = 101
    }
}
