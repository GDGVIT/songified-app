package com.dscvit.songified.network


import com.dscvit.songified.model.GetSongBpmResponse
import com.dscvit.songified.model.SongDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterfaceGSB {
//This is for getSongBPM api

    @GET("/search/")
    suspend fun searchSong(
        @Query("type") type: String,
        @Query("lookup") lookup: String,
        @Query("api_key") apiKey: String
    ): Response<GetSongBpmResponse>

    @GET("/song/")
    suspend fun getSongDetails(
        @Query("id") lookup: String,
        @Query("api_key") apiKey: String
    ):Response<SongDetailsResponse>

}