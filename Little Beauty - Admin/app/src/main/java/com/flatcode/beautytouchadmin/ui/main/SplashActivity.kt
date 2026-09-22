package com.flatcode.beautytouchadmin.ui.main

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouchadmin.ui.auth.LoginActivity
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null
    private val timeFinal = 2000L
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        lifecycleScope.launch {
            delay(timeFinal)
            checkUser()
        }
    }

    private fun checkUser() {
        if (!viewModel.isUserLoggedIn()) {
            openActivity<LoginActivity>()
        } else {
            openActivity<MainActivity>()
        }
        finish()
    }
}
