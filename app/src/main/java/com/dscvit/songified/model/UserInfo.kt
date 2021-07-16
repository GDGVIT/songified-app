package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class UserInfo(

    @SerializedName("level")
    val userLevel: String,
    @SerializedName("_id")
    val userId: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("thumbnail")
    val userImg: String,
    @SerializedName("email")
    val userEmail: String,
    @SerializedName("points")
    val userPoints: String,
    @SerializedName("songbookId")
    val songbooks: ArrayList<Songbook>

)
