package org.navgurukul.chat.core.extensions

import androidx.fragment.app.FragmentTransaction

inline fun androidx.fragment.app.FragmentManager.commitTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}
