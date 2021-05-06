package com.dscvit.songified.network

import com.dscvit.songified.model.*
import com.dscvit.songified.util.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ApiClient(
    private val apiSongified: ApiInterfaceSongified,
    private val apiGSB: ApiInterfaceGSB
) : BaseApiClient() {

//    suspend fun searchSong(type:String,lookup:String) = getResult {
//        api.searchSong(type,lookup,Constants.API_KEY)
//    }

    suspend fun login(signInRequest: SignInRequest) = getResult {
        apiSongified.login(signInRequest)
    }

    suspend fun addToSongBook(addToSongbookRequest: AddToSongbookRequest) = getResult {
        apiSongified.addToSongbook(addToSongbookRequest)
    }

    suspend fun getSongsInSongbook(getSingleSongbookRequest: SingleSongbookRequest) = getResult {
        apiSongified.getSongsInSongbook(getSingleSongbookRequest)
    }

    suspend fun uploadSongInfo(addSongInfoRequest: AddSongInfoRequest) = getResult {
        apiSongified.uploadSongInfo(addSongInfoRequest)
    }

    suspend fun getSongbooks() = getResult { apiSongified.getSongbooks() }

    suspend fun newSongbook(newSongbookRequest: NewSongbookRequest) = getResult {
        apiSongified.newSongbook(newSongbookRequest)
    }

    suspend fun getUserInfo() = getResult { apiSongified.getUserInfo() }

    suspend fun uploadSong(songName: RequestBody, filePart: MultipartBody.Part) =
        getResult { apiSongified.uploadSong(songName, filePart) }

    suspend fun getAnalysedData(analysedDataRequest: AnalysedDataRequest) = getResult {
        apiSongified.getAnalysedData(analysedDataRequest)
    }

    suspend fun getSongComments(songInfoRequest: SongInfoRequest) =
        getResult { apiSongified.getSongInfo(songInfoRequest) }

    suspend fun deleteSongInSongbook(songbookSongDeleteRequest: SongbookSongDeleteRequest) =
        getResult { apiSongified.deleteSongInSongbook(songbookSongDeleteRequest) }

    suspend fun deleteSongbook(songbookDeleteRequest: SongbookDeleteRequest) =
        getResult { apiSongified.deleteSongbook(songbookDeleteRequest) }

    suspend fun updateSongbookName(updateSongbookNameRequest: UpdateSongbookNameReqeust) =
        getResult { apiSongified.updateSongbookName(updateSongbookNameRequest) }

    suspend fun getPreviousUploads() = getResult { apiSongified.getPreviousUploads() }

    suspend fun logout() = getResult { apiSongified.logout() }
    suspend fun updateSongInSongbook(addToSongbookRequest: UpdateSongInSongbookRequest) =
        getResult { apiSongified.updateSongInSongbook(addToSongbookRequest) }

    //This is for getSongBPM api
    suspend fun searchSong(type: String, lookup: String) = getResult {
        apiGSB.searchSong(type, lookup, Constants.API_KEY)
    }

    suspend fun getSongInfo(id: String) = getResult {
        apiGSB.getSongDetails(id, Constants.API_KEY)
    }

}