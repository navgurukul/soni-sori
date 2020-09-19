package org.navgurukul.chat.core.utils

import java.io.File

internal fun String?.isLocalFile() = this != null && File(this).exists()
