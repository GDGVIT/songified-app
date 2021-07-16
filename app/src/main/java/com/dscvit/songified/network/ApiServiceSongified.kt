package com.dscvit.songified.network

import android.content.Context
import android.util.Log
import com.dscvit.songified.util.Constants
import com.dscvit.songified.util.PrefHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiServiceSongified {
    fun createRetrofit(context: Context): ApiInterfaceSongified {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(context))
            .build()

        return retrofit.create(ApiInterfaceSongified::class.java)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        val sharedPref = PrefHelper.customPrefs(context, Constants.PREF_NAME)

        val token = sharedPref.getString(Constants.PREF_AUTH_TOKEN, "")
        val tokenStr = if (token != "") {
            "Bearer $token"
        } else {
            ""
        }
        Log.d("ApiService", tokenStr)

        httpClient.connectTimeout(25, TimeUnit.SECONDS)
        httpClient.readTimeout(25, TimeUnit.SECONDS)
        httpClient.writeTimeout(30, TimeUnit.SECONDS)
        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.level = HttpLoggingInterceptor.Level.NONE
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .addHeader(
                    "Authorization",
                    tokenStr
                )

            val request = requestBuilder.build()

            return@addInterceptor chain.proceed(request)
        }
        return httpClient.build()
    }
}
