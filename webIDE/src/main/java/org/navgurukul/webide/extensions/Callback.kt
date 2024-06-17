package org.navgurukul.webide.extensions

import android.animation.Animator
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.AdapterView
import android.widget.Spinner
import androidx.drawerlayout.widget.DrawerLayout

fun Spinner.onItemSelected(onItemSelected: (position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            onItemSelected.invoke(p2)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }
}

fun DrawerLayout.onDrawerOpened(onDrawerOpened: () -> Unit) {
    addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerOpened(drawerView: View) {
            onDrawerOpened.invoke()
        }

        override fun onDrawerStateChanged(newState: Int) {}
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerClosed(drawerView: View) {}
    })
}

fun ViewPropertyAnimator.onAnimationStop(onAnimationStop: () -> Unit) {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            onAnimationStop.invoke()
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
}