package com.flatcode.beautytouch.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.ui.auth.AuthActivity
import com.flatcode.beautytouch.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    var context: Context = this@SplashActivity
    var auth: FirebaseAuth? = null
    var time_per_second = 2
    var time_final = time_per_millis * time_per_second

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        Handler().postDelayed({ checkUser() }, time_final.toLong())
    }

    private fun checkUser() {
        //get current user, if logged in
        val firebaseUser = auth!!.currentUser
        if (firebaseUser == null) {
            context.openActivity<AuthActivity>()
        } else {
            context.openActivity<MainActivity>()
        }
        finish()
    }

    companion object {
        const val time_per_millis = 1000
    }
}