package com.dscvit.songified.di

import com.dscvit.songified.di.modules.apiModule
import com.dscvit.songified.di.modules.repoModule
import com.dscvit.songified.di.modules.viewModelModule

val appComponents = listOf(apiModule, repoModule, viewModelModule)
