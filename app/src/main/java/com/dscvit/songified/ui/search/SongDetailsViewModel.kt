package com.dscvit.songified.ui.search

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.AddSongInfoRequest
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.SongInfoRequest
import com.dscvit.songified.repository.AppRepo

class SongDetailsViewModel(private val repo: AppRepo) : ViewModel() {
    fun getSongDetails(id: String) = repo.getSongDetails(id)
    fun addToSongbook(addToSongbookRequest: AddToSongbookRequest) =
        repo.addToSongbook(addToSongbookRequest)

    fun uploadSongInfo(addSongInfoRequest: AddSongInfoRequest) =
        repo.uploadSongInfo(addSongInfoRequest)

    fun getSongbooks() = repo.getSongbooks()
    fun getSongComments(songInfoRequest: SongInfoRequest) = repo.getSongComments(songInfoRequest)
}
