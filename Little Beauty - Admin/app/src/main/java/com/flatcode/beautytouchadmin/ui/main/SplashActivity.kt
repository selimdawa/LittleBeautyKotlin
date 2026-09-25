package com.flatcode.beautytouchadmin.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.flatcode.beautytouchadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouchadmin.ui.auth.LoginActivity
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private var binding: ActivitySplashBinding? = null
    private val timeFinal = 2000L
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        lifecycleScope.launch {
            delay(timeFinal.milliseconds)
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
