package org.navgurukul.typing.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.bottom_tab_layout.*
import org.navgurukul.typing.R
import org.navgurukul.typing.dashboard.advance.AdvanceFragment
import org.navgurukul.typing.dashboard.beginner.BeginnerFragment
import org.navgurukul.typing.dashboard.intermediate.IntermediateFragment


class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar =
            findViewById<View>(R.id.tool_bar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        //default view
        replaceFragment(BeginnerFragment.newInstance(), "BeginnerFragment")
        rg.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_home -> replaceFragment(BeginnerFragment.newInstance(), "BeginnerFragment")
                R.id.btn_youtube -> replaceFragment(IntermediateFragment.newInstance(), "IntermediateFragment")
                R.id.btn_status -> replaceFragment(AdvanceFragment.newInstance(), "AdvanceFragment")
            }
        })
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment, tag)
        ft.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}