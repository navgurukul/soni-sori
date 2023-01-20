package org.merakilearn.util.webide.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_project.view.*
import org.merakilearn.R
import org.merakilearn.extension.inflate
import org.merakilearn.extension.snack
import org.merakilearn.util.webide.net.HtmlParser
import org.merakilearn.util.webide.project.ProjectManager
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
        val itemView = parent.inflate(R.layout.item_project)
        return ProjectHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) =
            holder.bind(projects[holder.adapterPosition], holder.adapterPosition)

    override fun getItemCount(): Int = projects.size

    inner class ProjectHolder(var view: View) : RecyclerView.ViewHolder(view) {

        fun bind(project: String, position: Int) {
            with (view) {
                val properties = HtmlParser.getProperties(project)
                title.text = properties[0]
//                author.text = properties[1]
//                desc.text = properties[2]
//                favicon.setImageBitmap(ProjectManager.getFavicon(view.context, project))
                favicon.setImageResource(R.drawable.ic_web_file)

                projectLayout.setOnClickListener {
                    var intent: Intent? = null
                    try {
                        intent = Intent(context, Class.forName("org.navgurukul.webide.ui.activity.ProjectActivity"))
                        intent.putExtra("project" ,project)
                        context.startActivity(intent)
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }

                projectLayout.setOnLongClickListener {
                    AlertDialog.Builder(view.context)
                            .setTitle("${view.context.getString(R.string.delete)} $project?")
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
