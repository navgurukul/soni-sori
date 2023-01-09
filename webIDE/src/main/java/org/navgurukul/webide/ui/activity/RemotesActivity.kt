package org.navgurukul.webide.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_remotes.*
import kotlinx.android.synthetic.main.dialog_remote_add.view.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.git.GitWrapper
import org.navgurukul.webide.ui.adapter.RemotesAdapter
import java.io.File

class RemotesActivity : ThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remotes)
        setSupportActionBar(toolbar)

        val repo = File(intent.getStringExtra("project_file"))
        val remotesAdapter = RemotesAdapter(this, remotesLayout, repo)
        val layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(remotesList.context,
                layoutManager.orientation)
        remotesList.addItemDecoration(dividerItemDecoration)
        remotesList.layoutManager = layoutManager
        remotesList.adapter = remotesAdapter

        newRemote.setOnClickListener {
            val cloneView = View.inflate(this@RemotesActivity, R.layout.dialog_remote_add, null)

            AlertDialog.Builder(this@RemotesActivity)
                    .setTitle("Add remote")
                    .setView(cloneView)
                    .setPositiveButton(R.string.git_add) { _, _ ->
                        GitWrapper.addRemote(remotesLayout, repo, cloneView.remoteAddName.text.toString(), cloneView.remoteAddUrl.text.toString())
                        remotesAdapter.add(cloneView.remoteAddName.text.toString(), cloneView.remoteAddUrl.text.toString())
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }

        remotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    newRemote.show()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && newRemote.isShown) newRemote.hide()
            }
        })
    }
}
