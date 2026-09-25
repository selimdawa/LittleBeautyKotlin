package com.flatcode.beautytouch.ui.auth

import android.content.Context
import android.os.Bundle
import com.flatcode.beautytouch.databinding.ActivityAuthBinding
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.openActivity

class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener { context.openActivity<LoginActivity>() }
        binding.skipBtn.setOnClickListener { context.openActivity<RegisterActivity>() }
    }
}