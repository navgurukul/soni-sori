package org.merakilearn.core.extentions

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }