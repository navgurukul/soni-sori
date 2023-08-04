package org.navgurukul.webide.ui.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.navgurukul.webide.R
import org.navgurukul.webide.ui.fragment.IntroFragment

class IntroAdapter(context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val bgColors: IntArray = context.resources.getIntArray(R.array.bg_screens)
    private val images = intArrayOf(R.drawable.ic_intro_logo_n, R.drawable.ic_intro_editor, R.drawable.ic_intro_git, R.drawable.ic_intro_done)
    private val titles: Array<String> = context.resources.getStringArray(R.array.slide_titles)
    private val desc: Array<String> = context.resources.getStringArray(R.array.slide_desc)

    override fun getItem(position: Int) = IntroFragment.newInstance(Bundle().apply {
        putInt("position", position)
        putInt("bg", bgColors[position])
        putInt("image", images[position])
        putString("title", titles[position])
        putString("desc", desc[position])
    })

    override fun getCount(): Int = images.size
}
