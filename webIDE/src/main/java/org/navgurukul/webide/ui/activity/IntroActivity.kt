package org.navgurukul.webide.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_intro.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.extensions.intentFor
import org.navgurukul.webide.extensions.startAndFinish
import org.navgurukul.webide.extensions.withFlags
import org.navgurukul.webide.ui.adapter.IntroAdapter
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.set
import org.navgurukul.webide.util.net.HtmlCompat

class IntroActivity : AppCompatActivity() {

    private lateinit var introAdapter: IntroAdapter
    private lateinit var dots: Array<TextView?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setContentView(R.layout.activity_intro)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        addBottomDots(0)

        introAdapter = IntroAdapter(this, supportFragmentManager)
        introPager.adapter = introAdapter
        introPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)
                if (position == 3) {
                    btnNext.text = getString(R.string.start)
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.text = getString(R.string.next)
                    btnSkip.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        btnSkip.setOnClickListener { endIntro() }

        btnNext.setOnClickListener {
            val current = introPager.currentItem + 1
            if (current < 4) {
                introPager.currentItem = current
            } else {
                endIntro()
            }
        }
    }

    private fun endIntro() {
        defaultPrefs(this)["intro_done"] = true
        startAndFinish(intentFor<MainActivity>().withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(4)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = HtmlCompat.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive[currentPage])
            dotsLayout.addView(dots[i])
        }

        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive[currentPage])
    }
}


