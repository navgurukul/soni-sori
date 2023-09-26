package org.merakilearn.ui.playground

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.Constants
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File

import org.merakilearn.ui.ArduinoBlocklyActivity

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    var isLoading: Boolean = false
    lateinit var exportFile: File

    override fun getLayoutResId() = R.layout.fragment_playground

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(context, 4)
        initSearchListener()

        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        recycler_view.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))

        val adapter =
            PlaygroundAdapter(requireContext()) { playgroundItemModel, view, isLongClick ->
                if (isLongClick)
                    showUpPopMenu(playgroundItemModel.file, view)
                else
                    viewModel.selectPlayground(playgroundItemModel)

            }
        if (isLoading) showLoading() else dismissLoadingDialog()
        recycler_view.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) {
            adapter.setData(it.playgroundsList)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is PlaygroundViewEvents.OpenPythonPlayground -> navigator.openPlayground(
                    requireContext(), null, isFromCourse = false
                )
                is PlaygroundViewEvents.OpenTypingApp -> navigator.launchTypingApp(
                    requireActivity(),
                    Mode.Playground
                )
                is PlaygroundViewEvents.OpenPythonPlaygroundWithFile -> navigator.openPlaygroundWithFileContent(
                    requireActivity(),
                    file = it.file
                )
                is PlaygroundViewEvents.OpenScratch -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    startActivity(intent)
                }
                is PlaygroundViewEvents.OpenScratchWithFile -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    intent.putExtra(Constants.INTENT_EXTRA_KEY_FILE, it.file)
                    startActivity(intent)
                }
                is PlaygroundViewEvents.OpenArduinoBlockly -> {
                    val intent = Intent(requireContext(), ArduinoBlocklyActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.title_playground),
            R.attr.textPrimary
        )
    }

    private fun showUpPopMenu(file: File, view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.popup_menu_file_saved, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete file")
                            .setMessage("Are you sure you want to delete this file?")
                            .setPositiveButton("Delete") { _, _ ->
                                viewModel.handle(PlaygroundActions.DeleteFile(file))
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                }
                R.id.shareSavedFile -> {
                    try {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/x-python"
                        val uri = FileProvider.getUriForFile(
                            requireContext(),
                            "org.merakilearn.fileprovider",
                            file
                        )
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share File")
                        intent.putExtra(Intent.EXTRA_TEXT, "Sharing File")
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        startActivity(intent)  //Passing the intent Instead  create chooser for SecurityException
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "File sharing failed!", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.exportSavedFile -> {
                    var mimeType =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                    if (mimeType.isNullOrEmpty())
                        mimeType = "application/octet-stream"
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType
                        putExtra(Intent.EXTRA_TITLE, file.name)
                        exportFile = file
                    }
                    startActivityForResult(intent, 100)
                }
                R.id.shareAsUrl -> viewModel.handle(
                    PlaygroundActions.ShareAsUrl(
                        file,
                        requireContext()
                    )
                )

            }
            true
        }
        popup.show()
    }

    private fun initSearchListener() {
        search_view.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handle(PlaygroundActions.Query(query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handle(PlaygroundActions.Query(newText))
                return false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            try {
                val outputStream = requireContext().contentResolver.openOutputStream(fileUri!!)
                outputStream?.write(exportFile.readBytes())
                outputStream?.close()
                Toast.makeText(requireContext(), "File exported successfully!", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "File exported incorrectly!", Toast.LENGTH_SHORT)
                    .show()
                println(e.localizedMessage)
            }
        } else if (requestCode == 100 && resultCode != Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "File export failed!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(PlaygroundActions.RefreshLayout)
    }


}