package org.navgurukul.webide.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class AnalyzeAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = ArrayList<Fragment>()
    private val titleList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String, args: Bundle?) {
        fragment.arguments = args
        fragmentList.add(fragment)
        titleList.add(title)
    }

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getPageTitle(position: Int): CharSequence? = titleList[position]

    override fun getCount(): Int = fragmentList.size
}
