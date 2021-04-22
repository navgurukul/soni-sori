package org.navgurukul.typingguru.score

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_score.*
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.ui.KeyboardActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ScoreActivity : AppCompatActivity() {
    private lateinit var content : ArrayList<String>
    private lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        val intent = intent
        val wpm = intent.getIntExtra("wpm", 0)
        val timeTaken = intent.getLongExtra("time_taken", 0)
        content = intent.getStringArrayListExtra("content") as ArrayList<String>
        type = intent.getStringExtra("type") as String

        val noOfRightKey = intent.getIntExtra("noOfRightKey", 0)
        val noOfWrongKey = intent.getIntExtra("noOfWrongKey", 0)
        Timber.d("noOfRightKey : $noOfRightKey and noOfWrongKey : $noOfWrongKey")
        //Typing accuracy is defined as the percentage of correct entries out of the total entries typed
        //To calculate this mathematically, take the number of correct characters typed divided by the total number, multiplied by 100%.
        // So if you typed 90 out of 100 characters correctly you typed with 90% accuracy.47/47+3 *100
        val temp : Double = (noOfRightKey.toDouble()/(noOfRightKey + noOfWrongKey))
        val accuracy : Double = (temp)*100
        txt_accuracy.text = "${accuracy.toInt()}%"

        val ms = String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeTaken) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    timeTaken
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    timeTaken
                )
            )
        )
        txt_wpm.text = "$wpm"
        txt_time_taken.text = "$ms"

        speedometer.speedTo(wpm.toFloat())

        btn_back.setOnClickListener{finish()}
        btn_retake.setOnClickListener{
            val intent = Intent(this@ScoreActivity, KeyboardActivity::class.java)
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
}