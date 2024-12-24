package org.merakilearn.util.webide.project

import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.merakilearn.R
import java.util.*

object DataValidator {

    fun validateCreate(context: Context, name: TextInputLayout): Boolean {
        if (name.editText!!.text.toString().isEmpty()) {
            name.error = context.getString(R.string.name_error)
            return false
        }
//
//        if (author.editText!!.text.toString().isEmpty()) {
//            author.error = context.getString(R.string.author_error)
//            return false
//        }
//
//        if (description.editText!!.text.toString().isEmpty()) {
//            description.error = context.getString(R.string.desc_error)
//            return false
//        }
//
//        if (keywords.editText!!.text.toString().isEmpty()) {
//            keywords.error = context.getString(R.string.keywords_error)
//            return false
//        }

        return true
    }

    fun validateClone(context: Context, name: TextInputEditText, remote: TextInputEditText): Boolean {
        if (name.text.toString().isEmpty()) {
            name.error = context.getString(R.string.name_error)
            return false
        }

        if (remote.text.toString().isEmpty()) {
            remote.error = context.getString(R.string.remote_error)
            return false
        }

        return true
    }

    fun removeBroken(context: Context,objectsList: ArrayList<*>) {
        val iterator = objectsList.iterator()
        while (iterator.hasNext()) {
            val string = iterator.next() as String
            if (!ProjectManager.isValid(context,string)) {
                iterator.remove()
            }
        }
    }
}