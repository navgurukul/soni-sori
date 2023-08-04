package org.navgurukul.webide.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.webide.R
import org.navgurukul.webide.databinding.ActivityRemotesBinding
import org.navgurukul.webide.databinding.DialogRemoteAddBinding

import org.navgurukul.webide.git.GitWrapper
import org.navgurukul.webide.ui.adapter.RemotesAdapter
import java.io.File

class RemotesActivity : ThemedActivity() {

    private lateinit var binding : ActivityRemotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.include.toolbar)

        val repo = File(intent.getStringExtra("project_file"))
        val remotesAdapter = RemotesAdapter(this, binding.remotesLayout, repo)
        val layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(binding.remotesList.context,
                layoutManager.orientation)
        binding.remotesList.addItemDecoration(dividerItemDecoration)
        binding.remotesList.layoutManager = layoutManager
        binding.remotesList.adapter = remotesAdapter

        binding.newRemote.setOnClickListener {
            val cloneView = DialogRemoteAddBinding.inflate(LayoutInflater.from(this@RemotesActivity))

            AlertDialog.Builder(this@RemotesActivity)
                    .setTitle("Add remote")
                    .setView(cloneView.root)
                    .setPositiveButton(R.string.git_add) { _, _ ->
                        GitWrapper.addRemote(binding.remotesLayout, repo, cloneView.remoteAddName.text.toString(), cloneView.remoteAddUrl.text.toString())
                        remotesAdapter.add(cloneView.remoteAddName.text.toString(), cloneView.remoteAddUrl.text.toString())
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }

        binding.remotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.newRemote.show()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.newRemote.isShown) binding.newRemote.hide()
            }
        })
    }
}
