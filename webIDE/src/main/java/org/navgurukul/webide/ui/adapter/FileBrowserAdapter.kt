package org.navgurukul.webide.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.merakilearn.util.webide.ROOT_PATH
import org.navgurukul.webide.R
import org.navgurukul.webide.databinding.DialogInputSingleBinding
import org.navgurukul.webide.databinding.ItemFileBrowserBinding
import org.navgurukul.webide.databinding.ItemFileRootBinding
import org.navgurukul.webide.extensions.*
import org.navgurukul.webide.ui.viewmodel.ProjectViewModel
import org.navgurukul.webide.util.Constants
import org.navgurukul.webide.util.editor.Clipboard
import org.navgurukul.webide.util.editor.ResourceHelper
import timber.log.Timber
import java.io.File
import java.io.IOException

class FileBrowserAdapter(private val context: Context, private val projectName: String, private val mainView: View, private val projectViewModel: ProjectViewModel, private val listener: (File) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentDir = File(context.ROOT_PATH(), projectName)

    private var fileList: Array<File> = currentDir.listFiles()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_UP -> RootHolder(ItemFileRootBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        else -> ViewHolder(ItemFileBrowserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as RootHolder).bind()
        } else {
            (holder as ViewHolder).bind(fileList[position - 1])
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = when (position) {
        0 -> TYPE_UP
        else -> TYPE_FILE
    }

    override fun getItemCount() = currentDir.list().size + 1

    fun updateFiles() {
        fileList = currentDir.listFiles()
        notifyDataSetChanged()
    }

    private fun createFile() {
        val newFileRootView = DialogInputSingleBinding.inflate(LayoutInflater.from(context))
        newFileRootView.inputText.setHint(R.string.file_name)
        val newFileDialog = AlertDialog.Builder(context)
                .setTitle("New file")
                .setView(newFileRootView.root)
                .setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.cancel, null)
                .create()

        newFileDialog.show()
        newFileDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (newFileRootView.inputText.string().isEmpty()) {
                newFileRootView.inputText.error = "Please enter a file name"
            } else {
                newFileDialog.dismiss()
                val fileStr = newFileRootView.inputText.string()
                val newFile = File(currentDir, fileStr)
                try {
                    newFile.writeText("\n")
                    mainView.snack("Created $fileStr.", Snackbar.LENGTH_SHORT)
                } catch (e: IOException) {
                    Timber.e(e)
                    mainView.snack(e.toString(), Snackbar.LENGTH_SHORT)
                }
            }
        }
    }

    private fun createFolder() {
        val newFolderRootView =  DialogInputSingleBinding.inflate(LayoutInflater.from(context))
        newFolderRootView.inputText.setHint(R.string.folder_name)

        val newFolderDialog = AlertDialog.Builder(context)
                .setTitle("New folder")
                .setView(newFolderRootView.root)
                .setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.cancel, null)
                .create()

        newFolderDialog.show()
        newFolderDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (newFolderRootView.inputText.string().isEmpty()) {
                newFolderRootView.inputText.error = "Please enter a folder name"
            } else {
                newFolderDialog.dismiss()
                val folderStr = newFolderRootView.inputText.string()
                val newFolder = File(currentDir, folderStr)
                try {
                    newFolder.mkdirs()
                    mainView.snack("Created $folderStr.", Snackbar.LENGTH_SHORT)
                } catch (e: IOException) {
                    Timber.e(e)
                    mainView.snack(e.toString(), Snackbar.LENGTH_SHORT)
                }
            }
        }
    }

    private fun renameFile(file: File) {

        val renameRootView =  DialogInputSingleBinding.inflate(LayoutInflater.from(context))
        renameRootView.inputText.setHint(R.string.new_name)
        renameRootView.inputText.setText(file.name)

        val renameDialog = AlertDialog.Builder(context)
                .setTitle("Rename ${file.name}")
                .setView(renameRootView.root)
                .setPositiveButton("RENAME", null)
                .setNegativeButton(R.string.cancel, null)
                .create()

        renameDialog.show()
        renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (renameRootView.inputText.text.toString().isEmpty()) {
                renameRootView.inputText.error = "Please enter a name"
            } else {
                renameDialog.dismiss()
                val renameStr = renameRootView.inputText.text.toString()
                val rename = File(file.path.replace(file.name, renameStr))
                if (file.move(rename, mainView)) {
                    mainView.snack("Renamed ${file.name} to $renameStr.", Snackbar.LENGTH_SHORT)
                }
            }
        }
    }

    private fun paste() {
        val currentFile = Clipboard.currentFile
        when (Clipboard.type) {
            Clipboard.Type.COPY -> if (currentFile!!.copy(currentDir, mainView)) {
                mainView.snack("Successfully copied ${currentFile.name}.", Snackbar.LENGTH_SHORT)
            }

            Clipboard.Type.CUT -> if (currentFile!!.move(currentDir, mainView)) {
                mainView.snack("Successfully moved {currentFile.name}.", Snackbar.LENGTH_SHORT)
                Clipboard.currentFile = null
            }
        }
    }

    private fun deleteFile(file: File) {
        AlertDialog.Builder(context)
                .setTitle("${context.getString(R.string.delete)} ${file.name}?")
                .setPositiveButton(R.string.delete) { _, _ ->
                    var deleteFlag = true
                    projectViewModel.removeOpenFile(file.path)

                    mainView.snack("Deleted $file.") {
                        action("UNDO") {
                            deleteFlag = false
                            dismiss()
                        }

                        callback {
                            if (deleteFlag) {
                                try {
                                    file.deleteRecursively()
                                } catch (e: IOException) {
                                    Timber.e(e)
                                }
                            }
                        }
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    inner class RootHolder(itemView: ItemFileRootBinding) : RecyclerView.ViewHolder(itemView.root) {

        fun bind() = with (itemView) {

            setOnClickListener {
                if (currentDir.name != projectName) {
                    currentDir = currentDir.parentFile
                    updateFiles()
                }
            }

            setOnLongClickListener {
                val menu = PopupMenu(context, it)
                menu.menuInflater.inflate(R.menu.menu_file_options, menu.menu)
                menu.menu.findItem(R.id.action_copy).isVisible = false
                menu.menu.findItem(R.id.action_cut).isVisible = false
                menu.menu.findItem(R.id.action_rename).isVisible = false
                menu.menu.findItem(R.id.action_delete).isVisible = false
                menu.menu.findItem(R.id.action_paste).isEnabled = Clipboard.currentFile != null

                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_new_file -> createFile()
                        R.id.action_new_folder -> createFolder()
                        R.id.action_paste -> paste()
                    }

                    updateFiles()
                    true
                }

                menu.show()
                true
            }
        }
    }

    inner class ViewHolder(private val binding: ItemFileBrowserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File) = with (binding) {
            ResourceHelper.setIcon(fileBrowserIcon, file, 0xFF448AFF.toInt())
            fileBrowserName.text = file.name
            root.setOnClickListener {
                if (file.isDirectory) {
                    currentDir = file
                } else {
                    listener(file)
                }

                updateFiles()
            }

            root.setOnLongClickListener {
                val menu = PopupMenu(context, it)
                menu.menuInflater.inflate(R.menu.menu_file_options, menu.menu)

                menu.menu.findItem(R.id.action_new).isVisible = false
                menu.menu.findItem(R.id.action_paste).isVisible = false

                if (file.isFile && file.name == "index.html") {
                    menu.menu.findItem(R.id.action_rename).isVisible = false
                    menu.menu.findItem(R.id.action_delete).isVisible = false
                }

                menu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_rename -> renameFile(file)
                        R.id.action_copy -> Clipboard.update(file, Clipboard.Type.COPY, mainView)
                        R.id.action_cut -> Clipboard.update(file, Clipboard.Type.CUT, mainView)
                        R.id.action_paste -> paste()
                        R.id.action_delete -> deleteFile(file)
                    }

                    updateFiles()
                    true
                }

                menu.show()
                true
            }
        }
    }

    companion object {
        private const val TYPE_UP = 0
        private const val TYPE_FILE = 1
    }
}