package com.dscvit.songified.ui.audioanalysis

import androidx.lifecycle.ViewModel
import com.dscvit.songified.repository.AppRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadSongViewModel(private val repo: AppRepo) : ViewModel() {

    fun uploadSong(songName: RequestBody, filePart: MultipartBody.Part) =
        repo.uploadSong(songName, filePart)

    fun getPreviousUploads() = repo.getPreviousUploads()
}