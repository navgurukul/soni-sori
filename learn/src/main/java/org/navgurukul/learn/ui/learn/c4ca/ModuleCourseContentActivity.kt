package org.navgurukul.learn.ui.learn.c4ca

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.databinding.DataBindingUtil
import kotlinx.android.parcel.Parcelize
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityModuleCourseContentBinding

@Parcelize
data class ModuleCourseContentActivityArgs(val courseId: Int, val contentId: String?= null) : Parcelable

class ModuleCourseContentActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityModuleCourseContentBinding


    companion object {
        fun start(context: Context, courseId: Int, contentId: String? = null) {
            val intent = Intent(context, ModuleCourseContentActivity::class.java).apply {
                putExtras(ModuleCourseContentActivityArgs(courseId,  contentId).toBundle()!!)
            }
            context.startActivity(intent)
        }

        const val MODULE_URL_INDEX = 0
        const val PATHWAY_URL_INDEX = 1
        const val COURSE_URL_INDEX = 3
        const val CONTENT_URL_INDEX = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_module_course_content)
    }
}