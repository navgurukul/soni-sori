package org.navgurukul.typingguru.utils

import android.content.Context
import android.hardware.usb.UsbManager
import timber.log.Timber
import java.util.Random
import java.util.concurrent.TimeUnit

class Utility {
    private var keyMap: HashMap<String, Int> = HashMap()
    val TYPE_PRACTICE_TYPING = "practicetyping"

    private val alphabetList: ArrayList<String> by lazy {
        ArrayList()
    }

    fun getAlphabets() : ArrayList<String> {
        var c = 'a'
        while (c <= 'z') {
            alphabetList.add(c.toString())
            ++c
        }
        return alphabetList;
    }

    fun getKeyCodeByText(text: String?): Int? {
        return keyMap!![text]
    }

    fun calculateWPM(elapsedTime: Long, list: List<String?>): Int {
        val second = elapsedTime / 1000
        val size = list.size
        //wpm formula
        // (x characters/5)/1m = y wpm
        return (size.toDouble() / 5 / second * 60).toInt()
    }

    fun calculateWPM(elapsedTime: Long, list: ArrayList<java.util.ArrayList<String>>): Int {
        val second = elapsedTime / 1000
        var size = 0
        for (l in list) {
            size = size + l.size
        }
        //wpm formula
        // (x characters/5)/1m = y wpm
        return (size.toDouble() / 5 / second * 60).toInt()
    }

    fun generateRandomWordList(list: ArrayList<String>): ArrayList<String> {
        val r = Random()
        val result = ArrayList<String>()
        for (i in 0..7) {
            val s = list[r.nextInt(list.size)]
            result.add(s)
        }
        return result
    }

    fun convertMinutesToMMSS(second: Int): String {
        val millis = 1000 * second.toLong()
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millis)
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millis)
            )
        )
    }

    fun isOtgSupported(context: Context): Boolean {
        return try {
            val usbManager =
                context.getSystemService(Context.USB_SERVICE) as UsbManager
            Timber.d(
                "USB Manager : $usbManager"
            )
            if (usbManager == null) {
                return false
            }
            val isOtgSupported =
                context.packageManager.hasSystemFeature("android.hardware.usb.host")
            Timber.d(
                "is Otg Supported :$isOtgSupported"
            )
            isOtgSupported
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    init {
        keyMap["a"] = 29
        keyMap["s"] = 47
        keyMap["d"] = 32
        keyMap["f"] = 34
        keyMap["g"] = 35
        keyMap["h"] = 36
        keyMap["j"] = 38
        keyMap["k"] = 39
        keyMap["l"] = 40
        keyMap[";"] = 74
        keyMap[" "] = 62
        keyMap["q"] = 45
        keyMap["w"] = 51
        keyMap["e"] = 33
        keyMap["r"] = 46
        keyMap["t"] = 48
        keyMap["y"] = 53
        keyMap["u"] = 49
        keyMap["i"] = 37
        keyMap["o"] = 43
        keyMap["p"] = 44
        keyMap["z"] = 54
        keyMap["x"] = 52
        keyMap["c"] = 31
        keyMap["v"] = 50
        keyMap["b"] = 30
        keyMap["n"] = 42
        keyMap["m"] = 41
        keyMap[","] = 55
        keyMap["."] = 56
        keyMap["/"] = 76
        keyMap["tab"] = 61
        keyMap["1"] = 8
        keyMap["2"] = 9
        keyMap["3"] = 10
        keyMap["4"] = 11
        keyMap["5"] = 12
        keyMap["6"] = 13
        keyMap["7"] = 14
        keyMap["8"] = 15
        keyMap["9"] = 16
        keyMap["0"] = 7
    }
}