package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongInfoResponse(
    @SerializedName("userComments")
    val songComments: ArrayList<SongComment>
)
