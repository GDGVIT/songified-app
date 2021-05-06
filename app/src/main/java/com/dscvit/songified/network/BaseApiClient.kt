package com.dscvit.songified.network


import android.util.Log
import retrofit2.Response
import com.dscvit.songified.model.Result
import com.dscvit.songified.BuildConfig

open class BaseApiClient {

    protected suspend fun <T> getResult(request: suspend () -> Response<T>): Result<T> {
        try {
            val response = request()
            Log.d("BaseApi",response.toString())
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)

                } else {
                    Result.Error("Server response error")
                }
            } else {
                Result.Error("${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: e.toString()
            Log.d("BaseApi",errorMessage)
            return if (BuildConfig.DEBUG) {
                Result.Error("Network called failed with message $errorMessage")
            } else {
                Result.Error("Check your internet connection!")
            }
        }
    }
}