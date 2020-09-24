package org.merakilearn.datasource.network.model

import java.io.Serializable

data class LoginRequest(
    var idToken: String?,
    var mode: String = "android"
) : Serializable