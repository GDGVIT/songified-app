package com.dscvit.songified.repository

import com.dscvit.songified.model.AddSongInfoRequest
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.AnalysedDataRequest
import com.dscvit.songified.model.NewSongbookRequest
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.model.SongInfoRequest
import com.dscvit.songified.model.SongbookDeleteRequest
import com.dscvit.songified.model.SongbookSongDeleteRequest
import com.dscvit.songified.model.UpdateSongInSongbookRequest
import com.dscvit.songified.model.UpdateSongbookNameReqeust
import com.dscvit.songified.network.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppRepo(private val apiClient: ApiClient) : BaseRepo() {

//    fun searchSong(type:String,lookup:String) = makeRequest {
//        apiClient.searchSong(type,lookup)
//    }

    fun login(signInRequest: SignInRequest) = makeRequest {
        apiClient.login(signInRequest)
    }

    fun addToSongbook(addToSongbookRequest: AddToSongbookRequest) = makeRequest {
        apiClient.addToSongBook(addToSongbookRequest)
    }

    fun getSongsInSongbook(getSingleSongbookRequest: SingleSongbookRequest) = makeRequest {
        apiClient.getSongsInSongbook(getSingleSongbookRequest)
    }

    fun uploadSongInfo(addSongInfoRequest: AddSongInfoRequest) = makeRequest {
        apiClient.uploadSongInfo(addSongInfoRequest)
    }

    fun getSongbooks() = makeRequest { apiClient.getSongbooks() }

    fun newSongbook(newSongbookRequest: NewSongbookRequest) = makeRequest {
        apiClient.newSongbook(newSongbookRequest)
    }

    fun getUserInfo() = makeRequest { apiClient.getUserInfo() }

    fun uploadSong(songName: RequestBody, filePart: MultipartBody.Part) =
        makeRequest { apiClient.uploadSong(songName, filePart) }

    fun getAnalysedData(analysedDataRequest: AnalysedDataRequest) =
        makeRequest { apiClient.getAnalysedData(analysedDataRequest) }

    fun getSongComments(songInfoRequest: SongInfoRequest) =
        makeRequest { apiClient.getSongComments(songInfoRequest) }

    fun deleteSongInSongbook(songbookSongDeleteRequest: SongbookSongDeleteRequest) =
        makeRequest { apiClient.deleteSongInSongbook(songbookSongDeleteRequest) }

    fun deleteSongbook(songbookDeleteRequest: SongbookDeleteRequest) =
        makeRequest { apiClient.deleteSongbook(songbookDeleteRequest) }

    fun updateSongbookName(updateSongbookNameRequest: UpdateSongbookNameReqeust) =
        makeRequest { apiClient.updateSongbookName(updateSongbookNameRequest) }

    fun getPreviousUploads() = makeRequest { apiClient.getPreviousUploads() }

    fun logout() = makeRequest { apiClient.logout() }
    fun updateSongInSongbook(addToSongbookRequest: UpdateSongInSongbookRequest) =
        makeRequest { apiClient.updateSongInSongbook(addToSongbookRequest) }

    // This is for getSongBPM api
    fun searchSong(type: String, lookup: String) = makeRequest {
        apiClient.searchSong(type, lookup)
    }

    fun getSongDetails(id: String) = makeRequest {
        apiClient.getSongInfo(id)
    }
}
