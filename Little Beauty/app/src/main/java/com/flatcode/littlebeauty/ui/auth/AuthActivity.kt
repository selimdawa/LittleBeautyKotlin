package com.flatcode.littlebeauty.ui.auth

import android.content.Context
import android.os.Bundle
import com.flatcode.littlebeauty.databinding.ActivityAuthBinding
import com.flatcode.littlebeauty.utils.BaseActivity
import com.flatcode.littlebeauty.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private var binding: ActivityAuthBinding? = null

    var context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.loginBtn.setOnClickListener { context.openActivity<LoginActivity>() }
        binding!!.skipBtn.setOnClickListener { context.openActivity<RegisterActivity>() }
    }
}