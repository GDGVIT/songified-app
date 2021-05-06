package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("idToken")
    val idToken: String,
)