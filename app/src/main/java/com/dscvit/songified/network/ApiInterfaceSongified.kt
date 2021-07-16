package com.dscvit.songified.network

import com.dscvit.songified.model.AddSongInfoRequest
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.AddToSongbookResponse
import com.dscvit.songified.model.AnalysedDataRequest
import com.dscvit.songified.model.GeneralPostResponse
import com.dscvit.songified.model.GetSongBpmResponse
import com.dscvit.songified.model.NewSongbookRequest
import com.dscvit.songified.model.PreviousUploadsResponse
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.model.SignInResult
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.model.SingleSongbookResponse
import com.dscvit.songified.model.SongAnalysisResponse
import com.dscvit.songified.model.SongInfoRequest
import com.dscvit.songified.model.SongInfoResponse
import com.dscvit.songified.model.SongbookDeleteRequest
import com.dscvit.songified.model.SongbookResponse
import com.dscvit.songified.model.SongbookSongDeleteRequest
import com.dscvit.songified.model.UpdateSongInSongbookRequest
import com.dscvit.songified.model.UpdateSongbookNameReqeust
import com.dscvit.songified.model.UploadSongResponse
import com.dscvit.songified.model.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiInterfaceSongified {

//    @POST("search/song")
//    suspend fun searchSong(@Body songSearchRequest: SongSearchRequest): Response<ArrayList<Song>>

    @POST("auth/login")
    suspend fun login(@Body signInRequest: SignInRequest): Response<SignInResult>

    @POST("songbook/addSong")
    suspend fun addToSongbook(@Body addToSongbookRequest: AddToSongbookRequest): Response<AddToSongbookResponse>

    @GET("/search/")
    suspend fun searchSong(
        @Query("type") type: String,
        @Query("lookup") lookup: String,
        @Query("api_key") apiKey: String
    ): Response<GetSongBpmResponse>

    @POST("songInfo")
    suspend fun uploadSongInfo(@Body addSongInfoRequest: AddSongInfoRequest): Response<GeneralPostResponse>

    @GET("userInfo")
    suspend fun getSongbooks(): Response<SongbookResponse>

    @GET("userInfo")
    suspend fun getUserInfo(): Response<UserInfo>

    @POST("songbook")
    suspend fun getSongsInSongbook(@Body getSingleSongbookRequest: SingleSongbookRequest): Response<SingleSongbookResponse>

    @POST("songbook/create")
    suspend fun newSongbook(@Body newSongbookRequest: NewSongbookRequest): Response<GeneralPostResponse>

    @Multipart
    @POST("upload/song")
    suspend fun uploadSong(
        @Part("songName") songName: RequestBody,
        @Part filePart: MultipartBody.Part

    ): Response<UploadSongResponse>

    @POST("upload/getAnalysedData")
    suspend fun getAnalysedData(@Body analysedDataRequest: AnalysedDataRequest): Response<SongAnalysisResponse>

    @POST("search/songData")
    suspend fun getSongInfo(@Body songInfoRequest: SongInfoRequest): Response<SongInfoResponse>

    @HTTP(method = "DELETE", path = "songbook/deleteSong", hasBody = true)
    suspend fun deleteSongInSongbook(@Body songbookSongDeleteRequest: SongbookSongDeleteRequest): Response<GeneralPostResponse>

    @HTTP(method = "DELETE", path = "songbook/deleteSongbook", hasBody = true)
    suspend fun deleteSongbook(@Body songbookDeleteRequest: SongbookDeleteRequest): Response<GeneralPostResponse>

    @HTTP(method = "PATCH", path = "songbook/songbookName", hasBody = true)
    suspend fun updateSongbookName(@Body updateSongbookNameRequest: UpdateSongbookNameReqeust): Response<GeneralPostResponse>

    @GET("upload/getAllUploads")
    suspend fun getPreviousUploads(): Response<PreviousUploadsResponse>

    @GET("auth/logout")
    suspend fun logout(): Response<GeneralPostResponse>

    @HTTP(method = "PATCH", path = "songbook/updateSong", hasBody = true)
    suspend fun updateSongInSongbook(@Body addToSongbookRequest: UpdateSongInSongbookRequest): Response<GeneralPostResponse>
}
