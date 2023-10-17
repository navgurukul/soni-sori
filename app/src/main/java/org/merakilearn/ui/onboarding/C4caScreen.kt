package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil.setContentView
import com.google.android.material.textfield.TextInputEditText
import org.merakilearn.R
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.LoginRequestBody
import org.merakilearn.datasource.network.model.LoginResponseC4ca
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.merakilearn.BuildConfig
import org.merakilearn.MainActivity
import org.merakilearn.di.networkModule
import org.navgurukul.learn.ui.learn.CourseContentActivity
import retrofit2.converter.moshi.MoshiConverterFactory
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import org.navgurukul.learn.ui.learn.LearnFragment


class C4caScreen : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://merd-api.merakilearn.org/"
    }

    // Define your Retrofit instance
    lateinit var moshi: Moshi
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_c4ca_screen)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val userIDEditText = findViewById<TextInputEditText>(R.id.userIDEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)

        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        loginButton.setOnClickListener {
            val userId = userIDEditText.text.toString()
            val password = passwordEditText.text.toString()

            val authApiService = retrofit.create(SaralApi::class.java)

            val requestBody = LoginRequestBody(userId, password)

            val call = authApiService.authenticate(requestBody)

            call.enqueue(object : Callback<LoginResponseC4ca> {
                override fun onResponse(call: Call<LoginResponseC4ca>, response: Response<LoginResponseC4ca>) {


                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        startActivity(Intent(this@C4caScreen, EmptyActivity::class.java))
                    } else {
                        Toast.makeText(this@C4caScreen, "failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseC4ca>, t: Throwable) {
                    Toast.makeText(this@C4caScreen, "failed login", Toast.LENGTH_SHORT).show()

                }
            })
        }
    }
}
