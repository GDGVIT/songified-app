package com.dscvit.songified.ui.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.dscvit.songified.R

class FirstTimeIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_intro)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.intro_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}