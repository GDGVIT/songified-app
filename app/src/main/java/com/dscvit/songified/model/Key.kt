package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class Key(
    @SerializedName("values")
    val keys:ArrayList<String>,
    @SerializedName("confidences")
    val confidences:ArrayList<String>

)
