package org.merakilearn.util.webide.json

import android.content.Context
import android.graphics.Color
import org.json.JSONArray
import org.merakilearn.util.webide.editor.ProjectFiles

class ColorReader {

    data class ColorDef(val id: String, val pattern: Regex, val color: Int, val dark: Int)

    companion object {

        fun getColorDefs(context: Context, fileName: String): ArrayList<ColorDef> {
            val colorDefs = ArrayList<ColorDef>()
            val jsonString = ProjectFiles.readTextFromAsset(context, fileName)
            val defsArr = JSONArray(jsonString)
            for (i in 0 until defsArr.length()) {
                val obj = defsArr.getJSONObject(i)
                try{
                    colorDefs.add(
                        newColorDef(
                            obj.getString("id"),
                            obj.getString("pattern"),
                            obj.getString("color"),
                            obj.getString("dark")))
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

            return colorDefs
        }

        private fun newColorDef(id: String, pattern: String, color: String, dark: String) =
                ColorDef(id, pattern.toRegex(), Color.parseColor(color), Color.parseColor(dark))
    }
}