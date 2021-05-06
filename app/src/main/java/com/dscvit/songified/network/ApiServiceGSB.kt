package com.dscvit.songified.network

import android.content.Context
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.PrefHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiServiceGSB {
    fun createRetrofit(context: Context): ApiInterfaceGSB {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(context))
            .build()

        return retrofit.create(ApiInterfaceGSB::class.java)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        val sharedPref = PrefHelper.customPrefs(context, Constants.PREF_NAME)

        val token = sharedPref.getString(Constants.PREF_AUTH_TOKEN, "")
        val tokenStr = if (token != "") {
            "Bearer$token"
        } else {
            ""
        }

        httpClient.connectTimeout(25, TimeUnit.SECONDS)
        httpClient.readTimeout(25, TimeUnit.SECONDS)
        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.level=HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )

                .addHeader("X-API-KEY",Constants.API_KEY)
            val request = requestBuilder.build()

            return@addInterceptor chain.proceed(request)
        }
        return httpClient.build()
    }
}