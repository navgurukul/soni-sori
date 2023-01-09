package org.navgurukul.webide.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_pull.view.*
import kotlinx.android.synthetic.main.item_remote.view.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.extensions.inflate
import org.navgurukul.webide.git.GitWrapper
import java.io.File
import java.util.*

class RemotesAdapter(private val mainContext: Context, private val remotesView: View, private val repo: File) : RecyclerView.Adapter<RemotesAdapter.RemotesHolder>() {

    private val remotesList: ArrayList<String>? = GitWrapper.getRemotes(remotesView, repo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemotesHolder {
        val view = parent.inflate(R.layout.item_remote)
        return RemotesHolder(view)
    }

    override fun onBindViewHolder(holder: RemotesHolder, position: Int)  = holder.bind(remotesList!![position])

    override fun getItemCount(): Int = remotesList!!.size

    fun add(remote: String, url: String) {
        GitWrapper.addRemote(remotesView, repo, remote, url)
        remotesList!!.add(remote)
        notifyDataSetChanged()
    }

    inner class RemotesHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {

        fun bind(remote: String) {
            with (rootView) {
                remoteName.text = remote
                remoteUrl.text = GitWrapper.getRemoteUrl(remotesView, repo, remote)

                setOnClickListener {
                    val pullView = View.inflate(context, R.layout.dialog_pull, null)
                    pullView.remotesSpinner.adapter = ArrayAdapter(mainContext, android.R.layout.simple_list_item_1, remotesList!!)
                    AlertDialog.Builder(mainContext)
                            .setTitle("Fetch from remote")
                            .setView(pullView)
                            .setPositiveButton("FETCH") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                GitWrapper.fetch(context, remotesView, repo, pullView.remotesSpinner.selectedItem as String, pullView.pullUsername.text.toString(), pullView.pullPassword.text.toString())
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }

                setOnLongClickListener {
                    AlertDialog.Builder(mainContext)
                            .setTitle("Remove $remote?")
                            .setMessage("This remote will be removed permanently.")
                            .setPositiveButton(R.string.remove) { _, _ ->
                                GitWrapper.removeRemote(remotesView, repo, remote)
                                remotesList?.remove(remote)
                                notifyDataSetChanged()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()

                    true
                }
            }
        }
    }
}
