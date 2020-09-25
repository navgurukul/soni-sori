
package org.navgurukul.commonui.error

interface ErrorFormatter {
    fun toHumanReadable(throwable: Throwable?): String
}