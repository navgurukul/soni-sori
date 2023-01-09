package org.navgurukul.webide.ui.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_analyze.*
import kotlinx.android.synthetic.main.widget_toolbar.*
import org.navgurukul.webIDE.R
import org.navgurukul.webide.ui.adapter.AnalyzeAdapter
import org.navgurukul.webide.ui.fragment.analyze.AnalyzeFileFragment
import java.io.File

class AnalyzeActivity : ThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)
        toolbar.title = File(intent.getStringExtra("project_file")).name
        setSupportActionBar(toolbar)
        setupPager(analyzePager)
        analyzeTabs.setupWithViewPager(analyzePager)
    }

    private fun setupPager(analyzePager: ViewPager?) {
        val adapter = AnalyzeAdapter(supportFragmentManager)
        adapter.addFragment(AnalyzeFileFragment(), "FILES", intent.extras)
        analyzePager!!.adapter = adapter
    }
}
