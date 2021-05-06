package com.dscvit.songified.ui.userprofile

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.AddToSongbookRequest
import com.dscvit.songified.model.SingleSongbookRequest
import com.dscvit.songified.repository.AppRepo

class UserProfileViewModel (private val repo: AppRepo) : ViewModel() {
    fun getUserInfo()=repo.getUserInfo()

    fun logout()=repo.logout()


}