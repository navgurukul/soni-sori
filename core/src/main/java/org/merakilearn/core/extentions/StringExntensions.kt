package org.merakilearn.core.extentions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

val gson = Gson()

inline fun <reified T> String.objectify(): T {
    return gson.fromJson(this, object: TypeToken<T>() {}.type)
}