package org.navgurukul.webide.ui.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import org.navgurukul.webIDE.databinding.ActivityAnalyzeBinding
import org.navgurukul.webide.ui.adapter.AnalyzeAdapter
import org.navgurukul.webide.ui.fragment.analyze.AnalyzeFileFragment
import java.io.File

class AnalyzeActivity : ThemedActivity() {

    private lateinit var binding : ActivityAnalyzeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.toolbar.title = File(intent.getStringExtra("project_file")).name
        setSupportActionBar(binding.include.toolbar)
        setupPager(binding.analyzePager)
        binding.analyzeTabs.setupWithViewPager(binding.analyzePager)
    }

    private fun setupPager(analyzePager: ViewPager?) {
        val adapter = AnalyzeAdapter(supportFragmentManager)
        adapter.addFragment(AnalyzeFileFragment(), "FILES", intent.extras)
        analyzePager!!.adapter = adapter
    }
}
