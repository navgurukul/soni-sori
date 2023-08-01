package org.navgurukul.webide.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.navgurukul.webIDE.databinding.ItemFileProjectBinding
import org.navgurukul.webide.util.editor.ResourceHelper
import java.io.File

class FileAdapter(context: Context, private var openFiles: ArrayList<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, openFiles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = ItemFileProjectBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        ResourceHelper.setIcon(rootView.fileIcon, File(openFiles[position]), 0xffffffff.toInt())
        rootView.fileTitle.text = getPageTitle(position)
        rootView.fileTitle.setTextColor(0xffffffff.toInt())
        rootView.fileTitle.typeface = Typeface.DEFAULT_BOLD
        return rootView.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = ItemFileProjectBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        try {
            ResourceHelper.setIcon(rootView.fileIcon, File(openFiles[position]), 0xffffffff.toInt())
            rootView.fileTitle.text = getPageTitle(position)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return rootView.root
    }

    private fun getPageTitle(position: Int): CharSequence = File(openFiles[position]).name

    override fun getCount(): Int = openFiles.size

    fun update(newFiles: ArrayList<String>) {
        openFiles.clear()
        openFiles.addAll(newFiles)
        notifyDataSetChanged()
    }
}
