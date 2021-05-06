package com.dscvit.songified.ui.search

import androidx.lifecycle.ViewModel
import com.dscvit.songified.repository.AppRepo

class SearchResultViewModel(private val repo: AppRepo) : ViewModel() {
    fun searchSong(type: String, lookup: String) = repo.searchSong(type, lookup)
}