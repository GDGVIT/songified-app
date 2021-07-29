package com.dscvit.songified.di.modules

import com.dscvit.songified.repository.AppRepo
import org.koin.dsl.module

val repoModule = module {
    factory { AppRepo(get()) }
}
