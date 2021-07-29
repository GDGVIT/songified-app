package com.dscvit.songified.ui.songbook

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.model.SongbookSongDeleteRequest
import com.dscvit.songified.repository.AppRepo

class SingleSongbookViewModel(private val repo: AppRepo) : ViewModel() {
    fun addToSongbook(addToSongbookRequest: AddToSongbookRequest) =
        repo.addToSongbook(addToSongbookRequest)

    fun getSingleSongbook(singleSongbookRequest: SingleSongbookRequest) =
        repo.getSongsInSongbook(singleSongbookRequest)

    fun deleteSong(songbookSongDeleteRequest: SongbookSongDeleteRequest) =
        repo.deleteSongInSongbook(songbookSongDeleteRequest)
}
