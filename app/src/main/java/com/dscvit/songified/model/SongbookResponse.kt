package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongbookResponse(

    @SerializedName("songbookId")
    val songbookList:ArrayList<Songbook>
)
