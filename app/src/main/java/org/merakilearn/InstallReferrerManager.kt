package org.merakilearn

import android.app.Application
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.datasource.UserRepo

class InstallReferrerManager(val application: Application, val userRepo: UserRepo) {

    fun checkReferrer() {
        if (!userRepo.installReferrerFetched) {
            val referrerClient = InstallReferrerClient.newBuilder(application).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val response: ReferrerDetails = referrerClient.installReferrer
                            val referrerUrl: String = response.installReferrer
                            userRepo.installReferrer = referrerUrl
                            userRepo.installReferrerFetched = true

                            uploadInstallReferrer()
                        }
                    }

                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        } else if (!userRepo.installReferrerUploaded) {
            uploadInstallReferrer()
        }
    }

    private fun uploadInstallReferrer() {
        val currentUser = userRepo.getCurrentUser()
        val installReferrer = userRepo.installReferrer

        if (currentUser != null) {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                if (userRepo.updateProfile(currentUser, installReferrer)) {
                    userRepo.installReferrerUploaded = true
                }
            }
        }
    }

}
