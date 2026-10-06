package com.flatcode.littlebeautyadmin.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.flatcode.littlebeautyadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlebeautyadmin.utils.openActivity
import com.flatcode.littlebeautyadmin.databinding.ActivitySplashBinding
import com.flatcode.littlebeautyadmin.ui.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("CustomSplashScreen")
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