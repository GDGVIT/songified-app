package com.dscvit.songified.ui.songbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.*
import com.dscvit.songified.repository.AppRepo

class SongbookViewModel(private val repo: AppRepo) : ViewModel() {
    fun getSongbooks() = repo.getSongbooks()
    fun newSongbook(newSongbookRequest: NewSongbookRequest) = repo.newSongbook(newSongbookRequest)

    fun updateSongbookName(updateSongbookNameReqeust: UpdateSongbookNameReqeust) =
        repo.updateSongbookName(updateSongbookNameReqeust)

    fun deleteSongbook(songbookDeleteRequest: SongbookDeleteRequest) =
        repo.deleteSongbook(songbookDeleteRequest)



}