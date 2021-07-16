package com.dscvit.songified.ui.songbook

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.UpdateSongInSongbookRequest
import com.dscvit.songified.repository.AppRepo

class SongbookSongDetailViewModel(private val repo: AppRepo) : ViewModel() {
    fun editSongbook(addToSongbookRequest: UpdateSongInSongbookRequest) =
        repo.updateSongInSongbook(addToSongbookRequest)
}
