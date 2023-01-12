package org.navgurukul.webide.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.webIDE.R
import org.navgurukul.webIDE.databinding.ItemProjectBinding
import org.navgurukul.webide.extensions.intentFor
import org.navgurukul.webide.extensions.snack
import org.navgurukul.webide.extensions.withFlags
import org.navgurukul.webide.ui.activity.ProjectActivity
import org.navgurukul.webide.util.net.HtmlParser
import org.navgurukul.webide.util.project.ProjectManager
import java.util.*

class ProjectAdapter(private val mainContext: Context, private val projects: ArrayList<String>, private val layout: CoordinatorLayout, private val recyclerView: RecyclerView) : RecyclerView.Adapter<ProjectAdapter.ProjectHolder>() {

    fun insert(project: String) {
        projects.add(project)
        val position = projects.indexOf(project)
        notifyItemInserted(position)
        recyclerView.scrollToPosition(position)
    }

    fun remove(position: Int) {
        projects.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        projects.sort()
        val itemView = ItemProjectBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProjectHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) =
            holder.bind(projects[holder.adapterPosition], holder.adapterPosition)

    override fun getItemCount(): Int = projects.size

    inner class ProjectHolder(var binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: String, position: Int) {
            with (binding) {
                val properties = HtmlParser.getProperties(project)
                title.text = properties[0]
                author.text = properties[1]
                desc.text = properties[2]
                favicon.setImageBitmap(ProjectManager.getFavicon(binding.root.context, project))

                projectLayout.setOnClickListener {
                    with (mainContext) {
                        (this as AppCompatActivity).startActivityForResult(
                                intentFor<ProjectActivity>("project" to project)
                                    .withFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK).apply {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                                        }
                                    }, 0)
                    }
                }

                projectLayout.setOnLongClickListener {
                    AlertDialog.Builder(binding.root.context)
                            .setTitle("${binding.root.context.getString(R.string.delete)} $project?")
                            .setMessage(R.string.change_undone)
                            .setPositiveButton(R.string.delete) { _, _ ->
                                ProjectManager.deleteProject(project)
                                remove(position)
                                layout.snack("Deleted $project.")
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()

                    true
                }
            }
        }
    }
}
