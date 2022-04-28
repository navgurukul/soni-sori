package org.navgurukul.typingguru.score

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_score.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.activityArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.merakilearn.core.navigator.ModeNew
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.keyboard.KeyboardWordActivity

@Parcelize
data class NewScoreActivityArgs(
    val rightKeys: Int,
    val wrongKeys: Int,
    val timeTaken: Long,
    val mode: ModeNew,
) : Parcelable

class NewScoreActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, scoreActivityArgs: NewScoreActivityArgs): Intent {
            return Intent(context, NewScoreActivity::class.java).apply {
                putExtras(scoreActivityArgs.toBundle()!!)
            }
        }
    }

    private val scoreActivityArgs: NewScoreActivityArgs by activityArgs()
    private val viewModel: NewScoreViewModel by viewModel(parameters = { parametersOf(scoreActivityArgs) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)


        viewModel.viewState.observe(this, Observer {
            txt_accuracy.text = it.accuracy
            txt_time_taken.text = it.timeTaken
            txt_wpm.text = "${it.wpm}"
            speedometer.speedTo(it.wpm.toFloat())
        })

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        btn_retake.setOnClickListener {
            val intent = KeyboardWordActivity.newIntent(this, scoreActivityArgs.mode, true)
            startActivity(intent)
            finish()
        }
        btn_back_to_lessons.setOnClickListener {
            finish()
        }
    }
}