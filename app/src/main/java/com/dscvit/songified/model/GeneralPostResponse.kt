package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class GeneralPostResponse(

    @SerializedName("message")
    val message: String
)