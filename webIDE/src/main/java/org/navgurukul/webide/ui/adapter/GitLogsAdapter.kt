package org.navgurukul.webide.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.eclipse.jgit.revwalk.RevCommit
import org.navgurukul.webide.R
import org.navgurukul.webide.databinding.ItemGitLogBinding
import org.navgurukul.webide.extensions.snack

class GitLogsAdapter(private val gitLogs: List<RevCommit>?) : RecyclerView.Adapter<GitLogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemGitLogBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(gitLogs!![position])

    override fun getItemCount(): Int = gitLogs?.size ?: 0

    inner class ViewHolder(var view: ItemGitLogBinding) : RecyclerView.ViewHolder(view.root) {

        fun bind(commit: RevCommit) {
            with (view) {
                val msg = SpannableString(commit.fullMessage)
                var index = commit.fullMessage.indexOf('\n') + 1
                val fullShown = booleanArrayOf(false)

                if (index == 0) index = commit.fullMessage.length


                msg.setSpan(StyleSpan(Typeface.BOLD), 0, index, 0)
                root.setOnClickListener {
                    if (!fullShown[0]) {
                        commitName.typeface = Typeface.DEFAULT
                        commitName.text = msg
                        fullShown[0] = true
                    } else {
                        commitName.typeface = Typeface.DEFAULT_BOLD
                        commitName.text = commit.shortMessage
                        fullShown[0] = false
                    }
                }

                root.setOnLongClickListener {
                    val clipboard = root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("hash", commit.id.name)
                    clipboard.setPrimaryClip(clip)
                    root.snack(R.string.commit_hash_copy, Snackbar.LENGTH_SHORT)
                    true
                }

                commitName.text = commit.shortMessage
                commitName.typeface = Typeface.DEFAULT_BOLD
                commitAuthor.text = root.context.getString(R.string.git_user_format, commit.authorIdent.name, commit.authorIdent.emailAddress)
                commitDate.text = commit.authorIdent.`when`.toString()
                commitHash.text = commit.id.name
            }
        }
    }
}
