package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SignInResult (

    @SerializedName("success")
    val success:String,
    @SerializedName("authToken")
    val token:String
        )
