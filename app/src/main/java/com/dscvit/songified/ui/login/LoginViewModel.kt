package com.dscvit.songified.ui.login

import androidx.lifecycle.ViewModel
import com.dscvit.songified.model.SignInRequest
import com.dscvit.songified.repository.AppRepo

class LoginViewModel(private val repo: AppRepo) : ViewModel() {

    fun login(signInRequest: SignInRequest) = repo.login(signInRequest)
}