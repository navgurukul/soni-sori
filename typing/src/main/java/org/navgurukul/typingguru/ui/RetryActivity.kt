package org.navgurukul.typingguru.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.play.core.splitcompat.SplitCompat
import kotlinx.android.synthetic.main.activity_retry.*
import kotlinx.android.synthetic.main.activity_score.btn_back_to_lessons
import kotlinx.android.synthetic.main.activity_score.btn_retake
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typingguru.R

class RetryActivity : BaseActivity() {
    private lateinit var content : ArrayList<String>
    private lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retry)

        val intent = intent
        content = intent.getStringArrayListExtra("content") as ArrayList<String>
        type = intent.getStringExtra("type") as String

        btn_close.setOnClickListener{finish()}
        btn_retake.setOnClickListener{
            val intent = Intent(this@RetryActivity, KeyboardActivity::class.java)
            intent.putExtra("content", content)
            intent.putExtra("type", type.toString())
            intent.putExtra("retake", true)
            startActivity(intent)
            finish()
        }
        btn_back_to_lessons.setOnClickListener{
            //val intent = Intent(this@ScoreActivity, CourseDetailActivity::class.java)
            //startActivity(intent)
            finish()
        }
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}