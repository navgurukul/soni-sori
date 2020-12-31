package org.navgurukul.typing

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_keyboard.*
import org.navgurukul.typing.utils.Constants
import org.navgurukul.typing.utils.Logger
import org.navgurukul.typing.utils.Utility
import java.lang.invoke.MethodHandles.Lookup.PACKAGE
import java.util.*
import kotlin.collections.ArrayList


class KeyboardActivity : AppCompatActivity() {
    private val TAG : String = "KeyboardActivity"
    private var keyPressCounter : Int = 0
    private lateinit var itemsLayout: LinearLayout
    private lateinit var keyBoardLayout: RelativeLayout
    private var list = ArrayList<String>()
    private var lesson = 0
    private lateinit var key : String
    private var beginnerListData = java.util.ArrayList<java.util.ArrayList<String>>()
    private var start : Double = 0.0
    private var end : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)
        key = intent.getStringExtra("key")!!
        Logger.d(TAG, "Lesson Key : "+key)
        hideSystemUI()
        keyBoardLayout = findViewById(R.id.keyboard_container)
        beginnerListData = Utility.getDataByKey(key)
        if (lesson < beginnerListData.size) {
            list = beginnerListData.get(lesson)
        }
        preparePracticeListView(list)
        updatePracticeViewList()
    }

    private fun preparePracticeListView(list : ArrayList<String>) {
        itemsLayout = findViewById(R.id.ll_display_container)
        //remove if any existing child view
        itemsLayout.removeAllViews()
        for(item in list) {
            var view: View = if(key.equals("PRACT1")){
                layoutInflater.inflate(R.layout.layout_practice1, null)
            } else{
                layoutInflater.inflate(R.layout.layout_textview, null)
            }
            val tvItem: TextView = view.findViewById(R.id.text)
            tvItem.setText(item)
            itemsLayout.addView(view)
        }
    }

    private fun updatePracticeViewList() {
        if(list.size != keyPressCounter) {
            var v: View? = null
            v = itemsLayout.getChildAt(keyPressCounter)
            val tvItem: TextView = v.findViewById(R.id.text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvItem.setBackground(getDrawable(R.drawable.blue_text_background))
            }
            //update hand image
            if (img_left.tag.toString().contains(tvItem.text as String)) {
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(getHandDrawable(tvItem.text as String))
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.right_resting_hand)
                img_right2.visibility = View.INVISIBLE
                img_left2.visibility = View.INVISIBLE
            } else if(img_left2.tag.toString().contains(tvItem.text as String)) {
                img_left2.visibility = View.VISIBLE
                img_left2.setImageResource(getHandDrawable(tvItem.text as String))
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.right_resting_hand)
                img_right2.visibility = View.INVISIBLE
                img_left.visibility = View.INVISIBLE
            } else if(img_right.tag.toString().contains(tvItem.text as String)) {
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(getHandDrawable(tvItem.text as String))
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(R.drawable.left_resting_hand)
                img_right2.visibility = View.INVISIBLE
                img_left2.visibility = View.INVISIBLE
            } else if(img_right2.tag.toString().contains(tvItem.text as String)) {
                img_right2.visibility = View.VISIBLE
                img_right2.setImageResource(getHandDrawable(tvItem.text as String))
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(R.drawable.left_resting_hand)
                img_right.visibility = View.INVISIBLE
                img_left2.visibility = View.INVISIBLE
            } else {//space
                img_right.visibility = View.VISIBLE
                img_right.setImageResource(R.drawable.space)
                img_left.visibility = View.VISIBLE
                img_left.setImageResource(R.drawable.left_resting_hand)
                img_right2.visibility = View.INVISIBLE
                img_left2.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateView(isMatched : Boolean) {
        var v: View? = null
        v = itemsLayout.getChildAt(keyPressCounter)
        val tvItem: TextView = v.findViewById(R.id.text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isMatched) {
                tvItem.setBackground(getDrawable(R.drawable.green_text_background))
                tvItem.tag = "marked_green"
            } else {
                tvItem.setBackground(getDrawable(R.drawable.wrong_text_background))
                tvItem.tag = "marked_blue"
                tvItem.postDelayed(Runnable
                {
                    if (!tvItem.tag.equals("marked_green")) {
                        tvItem.setBackground(getDrawable(R.drawable.blue_text_background))
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
        if(keyPressCounter == 0) {
            start = Date().time.toDouble()
        }
        Logger.d(TAG, "Typing start time : $start")
        val id = resources.getIdentifier("key_btn_$keyCode", "id", packageName)
        Logger.d(TAG, "Resource id : $id")
        val btn : Button = findViewById(id)
        val pressedButtonText = btn.getText().toString()
        Logger.d(TAG, "pressedButtonText: $pressedButtonText")
        val hintText = list.get(keyPressCounter)
        Logger.d(TAG, "hintText: $hintText")
        if (pressedButtonText.equals(hintText, ignoreCase = true)) {
            Logger.d(TAG, "Key matched!!!")
            btn.setBackground(getDrawable(R.drawable.key_selector))
            updateView(true)
            keyPressCounter++
            updatePracticeViewList()
        } else {
            Logger.e(TAG,"Key does not match")
            btn.setBackground(getDrawable(R.drawable.key_wrong_selector))
            updateView(false)
        }
        simulateButtonDown(btn)
        btn.postDelayed(Runnable
        {
            simulateButtonUp(btn)
        }, 300)

        if(list.size == keyPressCounter) {
            refreshView()
        }
        return true
    }

    private fun refreshView() {
        keyPressCounter = 0
        //go to next lesson
        lesson++
        if (lesson >= beginnerListData.size) {
            Logger.d(TAG, "Lesson $lesson completed")
            end = Date().time.toDouble()
            Logger.d(TAG, "Typing end time after completing lesson : $end")
            val elapsedTime = end-start
            Logger.d(TAG, "Typing elapsed time : $elapsedTime")
            val wpm = Utility.calculateWPM(elapsedTime, beginnerListData)
            Logger.d(TAG, "WPM : $wpm")
            Toast.makeText(this, "Lesson completed", Toast.LENGTH_SHORT).show()
            val intent : Intent = Intent()
            intent.putExtra("key", key)
            intent.putExtra("wpm", wpm)
            setResult(Constants.REQUEST_CODE_BEGINNER, intent)
            finish()
        } else {
            list = beginnerListData.get(lesson)
            preparePracticeListView(list)
            updatePracticeViewList()
        }
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
        val resID = resources.getIdentifier("wrong_key", "raw", packageName)
        val mediaPlayer = MediaPlayer.create(this, resID)
        mediaPlayer.start()
    }

    private fun getHandDrawable(name: String): Int {
        return resources.getIdentifier("hand_img_$name", "drawable", packageName)
    }
}