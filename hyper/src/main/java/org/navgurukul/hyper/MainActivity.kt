package org.navgurukul.hyper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode

@Parcelize
data class MainActivityArgs(
    val mode: Mode,
    val retake: Boolean = false
) : Parcelable

class MainActivity : AppCompatActivity() {

    companion object {

        fun newIntent(context: Context, mode: Mode, retake: Boolean = false): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtras(MainActivityArgs(mode, retake).toBundle()!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}