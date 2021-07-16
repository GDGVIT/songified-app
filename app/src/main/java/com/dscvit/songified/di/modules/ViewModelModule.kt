package com.dscvit.songified.di.modules

import com.dscvit.songified.ui.audioanalysis.AudioAnalysisViewModel
import com.dscvit.songified.ui.audioanalysis.UploadSongViewModel
import com.dscvit.songified.ui.login.LoginViewModel
import com.dscvit.songified.ui.search.SearchResultViewModel
import com.dscvit.songified.ui.search.SearchViewModel
import com.dscvit.songified.ui.search.SongDetailsViewModel
import com.dscvit.songified.ui.songbook.SingleSongbookViewModel
import com.dscvit.songified.ui.songbook.SongbookSongDetailViewModel
import com.dscvit.songified.ui.songbook.SongbookViewModel
import com.dscvit.songified.ui.userprofile.UserProfileViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { SearchViewModel(get()) }
    viewModel { SearchResultViewModel(get()) }
    viewModel { SongDetailsViewModel(get()) }
    viewModel { SongbookViewModel(get()) }
    viewModel { SingleSongbookViewModel(get()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { AudioAnalysisViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { UploadSongViewModel(get()) }
    viewModel { SongbookSongDetailViewModel(get()) }
}
