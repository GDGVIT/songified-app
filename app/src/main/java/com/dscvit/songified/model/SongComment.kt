package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongComment(
    @SerializedName("verified")
    val verified: Boolean,
    @SerializedName("user")
    val user: CommentOwner,
    @SerializedName("detail")
    val comment: String
)
