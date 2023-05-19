package org.navgurukul.typingguru.score

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.parcel.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.activityArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.databinding.ActivityScoreBinding
import org.navgurukul.typingguru.keyboard.KeyboardActivity

@Parcelize
data class ScoreActivityArgs(
    val rightKeys: Int,
    val wrongKeys: Int,
    val timeTaken: Long,
    val mode: Mode,
) : Parcelable

class ScoreActivity : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, scoreActivityArgs: ScoreActivityArgs): Intent {
            return Intent(context, ScoreActivity::class.java).apply {
                putExtras(scoreActivityArgs.toBundle()!!)
            }
        }
    }

    private val scoreActivityArgs: ScoreActivityArgs by activityArgs()
    private val viewModel: ScoreViewModel by viewModel(parameters = { parametersOf(scoreActivityArgs) })
    private lateinit var mBinding : ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_score )


        viewModel.viewState.observe(this, Observer {
            mBinding.apply {
                txtAccuracy.text = it.accuracy
                txtTimeTaken.text = it.timeTaken
                txtWpm.text = "${it.wpm}"
                speedometer.speedTo(it.wpm.toFloat())
            }
        })

        setSupportActionBar(mBinding.toolbar)

        mBinding.toolbar.setNavigationOnClickListener { finish() }

        mBinding.btnRetake.setOnClickListener {
            val intent = KeyboardActivity.newIntent(this, scoreActivityArgs.mode, true)
            startActivity(intent)
            finish()
        }
        mBinding.btnBackToLessons.setOnClickListener {
            finish()
        }
    }
}