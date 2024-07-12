package org.merakilearn.util.webide.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.databinding.ItemProjectBinding
import org.merakilearn.extension.inflate
import org.merakilearn.extension.snack
import org.merakilearn.util.webide.project.ProjectManager
import org.merakilearn.util.webide.net.HtmlParser

class ProjectAdapter(
    private val mainContext: FragmentActivity,
    private val navigator: MerakiNavigator,
    private val projects: ArrayList<String>,
    private val layout: CoordinatorLayout,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<ProjectAdapter.ProjectHolder>() {

    fun insert(project: String) {
        projects.add(project)
        val position = projects.indexOf(project)
        notifyItemInserted(position)
        recyclerView.scrollToPosition(position)
    }

    fun remove(position: Int) {
        try {
            projects.removeAt(position)
        } catch (e: Exception) {
            e.printStackTrace()
            projects.clear()
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) =
        holder.bind(projects[holder.adapterPosition], holder.adapterPosition)

    override fun getItemCount(): Int = projects.size

    inner class ProjectHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: String, position: Int) {
            val properties = HtmlParser.getProperties(itemView.context, project)
            binding.title.text = properties[0]
            binding.favicon.setImageResource(R.drawable.ic_web_file)

                binding.projectLayout.setOnClickListener {
                    var intent: Intent? = null
                    try {
                        navigator.launchWebIDEApp(mainContext, project)
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }

                binding.projectLayout.setOnLongClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("${itemView.context.getString(R.string.delete)} $project?")
                        .setMessage(R.string.change_undone)
                        .setPositiveButton(R.string.delete) { _, _ ->
                            ProjectManager.deleteProject(itemView.context, project)
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
