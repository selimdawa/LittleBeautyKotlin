package com.flatcode.beautytouch.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.flatcode.beautytouch.databinding.ActivitySplashBinding
import com.flatcode.beautytouch.ui.auth.AuthActivity
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.openActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val context: Context = this@SplashActivity
    private var auth: FirebaseAuth? = null
    private val timeFinal = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        Handler(Looper.getMainLooper()).postDelayed({ checkUser() }, timeFinal.toLong())
    }

    private fun checkUser() {
        //get current user, if logged in
        val firebaseUser = auth?.currentUser
        if (firebaseUser == null) {
            context.openActivity<AuthActivity>()
        } else {
            context.openActivity<MainActivity>()
        }
        finish()
    }
}