package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class Songbook(

    @SerializedName("id")
    val id:String,
    @SerializedName("songbookName")
    val name:String
)
