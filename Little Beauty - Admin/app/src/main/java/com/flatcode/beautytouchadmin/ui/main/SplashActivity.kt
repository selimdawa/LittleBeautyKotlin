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
    private val context: Context = this@SplashActivity
    private val time_per_second = 2
    private val time_final = time_per_millis * time_per_second
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        lifecycleScope.launch {
            delay(time_final.toLong())
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

    companion object {
        const val time_per_millis = 1000
    }
}
