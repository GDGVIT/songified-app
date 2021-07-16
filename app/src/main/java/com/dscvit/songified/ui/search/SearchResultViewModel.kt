package com.dscvit.songified.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.GetSongBpmResponse
import com.dscvit.songified.model.Result
import com.dscvit.songified.repository.AppRepo

class SearchResultViewModel(private val repo: AppRepo) : ViewModel() {

    // var searchResults:MutableLiveData<Result<GetSongBpmResponse>> =  MutableLiveData<Result<GetSongBpmResponse>>()
    val mTag = "SearchResultViewModel"
    val searchResults: MutableLiveData<Result<GetSongBpmResponse>> by lazy {
        MutableLiveData<Result<GetSongBpmResponse>>()
    }
    fun searchSong(type: String, lookup: String) = repo.searchSong(type, lookup)
}
