package com.dscvit.songified.di

import com.dscvit.songified.di.modules.viewModelModule
import com.dscvit.songified.di.modules.apiModule
import com.dscvit.songified.di.modules.repoModule

val appComponents = listOf(apiModule, repoModule, viewModelModule)