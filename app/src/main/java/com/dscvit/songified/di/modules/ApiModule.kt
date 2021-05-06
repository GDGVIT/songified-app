package com.dscvit.songified.di.modules

import com.dscvit.songified.network.ApiClient
import com.dscvit.songified.network.ApiServiceSongified
import com.dscvit.songified.network.ApiServiceGSB
import org.koin.dsl.module

val apiModule = module {
    factory { ApiServiceSongified.createRetrofit(get()) }
    factory { ApiServiceGSB.createRetrofit(get()) }
    factory { ApiClient(get(),get()) }

}