package org.navgurukul.typingguru.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import kotlinx.android.synthetic.main.activity_keyboard.*
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.score.ScoreActivity
import org.navgurukul.typingguru.utils.Logger
import org.navgurukul.typingguru.utils.Utility
import java.util.*
import kotlin.collections.ArrayList


class KeyboardActivity : AppCompatActivity() {
    private val TAG : String = "KeyboardActivity"
    private var keyPressCounter : Int = 0
    private lateinit var itemsLayout: LinearLayout
    private lateinit var keyBoardLayout: RelativeLayout
    private var list = ArrayList<String>()
    private var lesson = 0
    //private var typingListData = java.util.ArrayList<java.util.ArrayList<String>>()
    private var start : Long = 0
    private var end : Long = 0
    private lateinit var content: ArrayList<String>
    private lateinit var type : String
    private var retake : Boolean = false
    private var isTypingStarted = false;
    private var practiceListData = java.util.ArrayList<java.util.ArrayList<String>>()

    private val TYPE_TRY_TYPING = "trytyping"
    private val TYPE_PRACTICE_TYPING = "practicetyping"
    private val PRACTICE_TIME : Int = 180 // 3 minutes
    private var currentTime = 0
    private val interval : Long = 1000 // 1 Second
    private var noOfWrongKey : Int = 0
    private var noOfRightKey : Int = 0

    private val handler: Handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            currentTime += 1
            Logger.d(TAG, "Current time $currentTime")
            txt_timer.text = "${Utility.convertMinutesToMMSS(currentTime)}/3:00"
            if (currentTime < PRACTICE_TIME) {
                handler.postDelayed(this, interval);
            } else {
                navigateToScoreOrRetryActivity()
            }
        }
    }

    //this method is required to get intent from other module
    companion object {
        fun newIntent(context: Context, content : ArrayList<String>, type : String): Intent {
            return Intent(context, KeyboardActivity::class.java).apply {
                putExtra("content", content)
                putExtra("type", type)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)

        hideSystemUI()
        keyBoardLayout = findViewById(R.id.keyboard_container)

        val intent = intent
        list.clear()
        content = intent.getStringArrayListExtra("content") as ArrayList<String>
        list = content
        type = intent.getStringExtra("type") as String
        retake = intent.getBooleanExtra("retake", false)
        Logger.d(TAG, "type : $type")
        preparePracticeListView(list)
        updatePracticeViewList()
        progress.max = list.size

        btn_back.setOnClickListener {
            Logger.d(TAG, "back button clicked")
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent()
                    .setClassName(packageName, "org.navgurukul.typingguru.ui.WebViewActivity")
            startActivity(intent)
        }
        if (!retake) {
            showInfoDialog()
        }
        if(type == TYPE_PRACTICE_TYPING) {
            txt_timer.visibility = View.VISIBLE
            handler.postAtTime(runnable, System.currentTimeMillis() + interval);
            handler.postDelayed(runnable, interval);
            start = Date().time
            practiceListData.add(list)
        } else {
            txt_timer.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        currentTime = 0
        lesson = 0
        isTypingStarted = false
        practiceListData.clear()
        noOfRightKey = 0
        noOfWrongKey = 0
    }

    override fun onStop() {
        super.onStop()
    }

    private fun showInfoDialog() {
        try {
            val inflater: LayoutInflater = getLayoutInflater()
            val alertLayout: View = inflater.inflate(R.layout.layout_info_dialog, null)
            val btnAccept: AppCompatButton = alertLayout.findViewById(R.id.btn_ok) as AppCompatButton
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setView(alertLayout)
            builder.setCancelable(true)
            val btAlertDialog: android.app.AlertDialog? = builder.create()
            btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            btnAccept.setOnClickListener {
                if (btAlertDialog != null && btAlertDialog.isShowing) {
                    btAlertDialog.dismiss()
                }
            }
            btAlertDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun preparePracticeListView(list : ArrayList<String>) {
        itemsLayout = findViewById(R.id.ll_display_container)
        //remove if any existing child view
        itemsLayout.removeAllViews()
        for(item in list) {
            var view: View = if(type.equals(TYPE_PRACTICE_TYPING)){
                layoutInflater.inflate(R.layout.layout_textview, null)
            } else{
                layoutInflater.inflate(R.layout.layout_textview, null)
            }
            //var view: View =layoutInflater.inflate(R.layout.layout_textview, null)
            val tvItem: TextView = view.findViewById(R.id.text)
            tvItem.setText(item)
            itemsLayout.addView(view)
        }
    }
    var previousKey : Int = 0
    private fun updatePracticeViewList() {
        if(list.size != keyPressCounter) {
            var v: View? = null
            v = itemsLayout.getChildAt(keyPressCounter)
            val tvItem: TextView = v.findViewById(R.id.text)
            val tvBottomHighLight: View = v.findViewById(R.id.bottom_high_light)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvBottomHighLight.visibility = View.VISIBLE
            }
            //highlight current keyboard key
            if (previousKey != 0) {
                val btn: AppCompatButton = findViewById(previousKey)
                simulateButtonUp(btn)
            }
            val id = resources.getIdentifier("key_btn_${Utility.getKeyCodeByText(tvItem.text as String)}", "id", "org.merakilearn.typing")
            Logger.d(TAG, "Resource id : $id")
            if(id != 0) {
                val btn: AppCompatButton = findViewById(id)
                simulateButtonDown(btn)
                previousKey = id
            }
            //update hand image
            if (img_left.tag.toString().contains(tvItem.text as String)) {
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(getHandDrawable(tvItem.text as String))
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.right_resting_hand)
                img_left2.visibility = View.INVISIBLE
            } else if(img_left2.tag.toString().contains(tvItem.text as String)) {
                img_left.visibility = View.INVISIBLE
                img_left2.visibility = View.VISIBLE
                img_left2.setImageResource(getHandDrawable(tvItem.text as String))
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.right_resting_hand)
            } else if(img_right.tag.toString().contains(tvItem.text as String)) {
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(getHandDrawable(tvItem.text as String))
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(R.drawable.left_resting_hand)
                img_left2.visibility = View.INVISIBLE
            } else {//space
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.space)
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(R.drawable.left_resting_hand)
                img_left2.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateView(isMatched : Boolean) {
        var v: View? = null
        v = itemsLayout.getChildAt(keyPressCounter)
        val tvItem: TextView = v.findViewById(R.id.text)
        val tvBottomHighLight: View = v.findViewById(R.id.bottom_high_light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isMatched) {
                tvItem.setBackground(getDrawable(R.drawable.correct_text_background))
                tvItem.tag = "marked_green"
                tvBottomHighLight.visibility = View.INVISIBLE
            } else {
                tvItem.setBackground(getDrawable(R.drawable.wrong_text_background))
                tvItem.tag = "marked_blue"
                tvItem.postDelayed(Runnable
                {
                    if (!tvItem.tag.equals("marked_green")) {
                        if (type == TYPE_PRACTICE_TYPING) {
                            tvBottomHighLight.visibility = View.INVISIBLE
                            tvItem.setBackground(getDrawable(R.drawable.wrong_text_background))
                        } else {
                            tvItem.setBackground(getDrawable(R.drawable.rounded_border))
                        }
                    }
                }, 300)
                //shake view
                tvItem.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
                //play wrong beep
                playWrongKeyPressAudio()
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        super.onKeyUp(keyCode, event)
        Logger.d(TAG, "Key code Up : $keyCode")
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        //to calculate wpm
        Logger.d(TAG, "isTypingStarted : $isTypingStarted")
        if(!isTypingStarted) {
            //start = Date().time
            isTypingStarted = true
        }
        Logger.d(TAG, "Typing start time : $start")
        val id = resources.getIdentifier("key_btn_$keyCode", "id", "org.merakilearn.typing")
        Logger.d(TAG, "Resource id : $id")
        val btn : AppCompatButton = findViewById(id)
        val pressedButtonText = btn.getText().toString()
        Logger.d(TAG, "pressedButtonText: $pressedButtonText")
        val hintText = list.get(keyPressCounter)
        Logger.d(TAG, "hintText: $hintText")
        if (pressedButtonText.equals(hintText, ignoreCase = true)) {
            Logger.d(TAG, "Key matched!!!")
            noOfRightKey++
            btn.setBackground(getDrawable(R.drawable.key_selector))
            updateView(true)
            keyPressCounter++
            updatePracticeViewList()
            //update progress bar
            progress.progress = keyPressCounter
        } else {
            Logger.e(TAG,"Key does not match")
            noOfWrongKey++
            btn.setBackground(getDrawable(R.drawable.key_wrong_selector))
            updateView(false)
            if (type == TYPE_PRACTICE_TYPING) {
                keyPressCounter++
                updatePracticeViewList()
                //update progress bar
                progress.progress = keyPressCounter
            }
        }
        simulateButtonDown(btn)
        btn.postDelayed(Runnable
        {
            if(btn.id != previousKey) {
                simulateButtonUp(btn)
            }
        }, 300)

        if(list.size == keyPressCounter) {
            navigateToScoreOrRetryActivity()
        }
        return true
    }

    private fun navigateToScoreOrRetryActivity() {
        Logger.d(TAG, "Lesson completed")
        //if type is try typing then navigate to try again activity
        var intent: Intent? = null
        if (type == TYPE_TRY_TYPING) {
            intent = Intent(this@KeyboardActivity, RetryActivity::class.java)
            intent.putExtra("content", content)
            intent.putExtra("type", type)
            startActivity(intent)
            finish()
        } else {
            if (currentTime == PRACTICE_TIME) {
                //after completing go to score activity
                //calculate wpm
                isTypingStarted = false
                end = Date().time
                Logger.d(TAG, "Typing end time after completing lesson : $end")
                val elapsedTime = end-start
                Logger.d(TAG, "Typing elapsed time : $elapsedTime")
                val wpm = Utility.calculateWPM(elapsedTime, practiceListData)
                Logger.d(TAG, "WPM : $wpm")
                Toast.makeText(this, "Lesson completed", Toast.LENGTH_SHORT).show()
                intent = Intent(this@KeyboardActivity, ScoreActivity::class.java)
                intent.putExtra("wpm", wpm)
                intent.putExtra("time_taken", elapsedTime)
                intent.putExtra("content", content)
                intent.putExtra("type", type)
                intent.putExtra("noOfRightKey", noOfRightKey)
                intent.putExtra("noOfWrongKey", noOfWrongKey)
                startActivity(intent)
                finish()
            } else {
                //create new lesson for practicing
                lesson++
                refreshView(true)
            }
        }
    }

    private fun refreshView(isPractice : Boolean) {
        keyPressCounter = 0
        if(isPractice) {
            list = Utility.generateRandomWordList(content)
            progress.max = list.size
            practiceListData.add(list)
        }
        preparePracticeListView(list)
        updatePracticeViewList()
        progress.progress = keyPressCounter
    }

    private fun simulateButtonDown(view : View) {
        view.setPressed(true);
        view.invalidate();
    }

    private fun simulateButtonUp(view : View) {
        view.setPressed(false);
        view.invalidate();
    }

    private fun hideSystemUI() {
        if (getWindow() != null) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            or View.SYSTEM_UI_FLAG_IMMERSIVE)
        }
    }

    private fun playWrongKeyPressAudio() {
        val resID = resources.getIdentifier("wrong_key", "raw", "org.merakilearn.typing")
        val mediaPlayer = MediaPlayer.create(this, resID)
        mediaPlayer.start()
    }

    private fun getHandDrawable(name: String): Int {
        if(name == ";") {
            return resources.getIdentifier("hand_img_semicolon", "drawable", "org.merakilearn.typing")
        } else if (name == ",") {
            return resources.getIdentifier("hand_img_comma", "drawable", "org.merakilearn.typing")
        } else if(name == ".") {
            return resources.getIdentifier("hand_img_period", "drawable", "org.merakilearn.typing")
        } else if(name == "/") {
            return resources.getIdentifier("hand_img_slash", "drawable", "org.merakilearn.typing")
        }
        return resources.getIdentifier("hand_img_$name", "drawable", "org.merakilearn.typing")
    }
}