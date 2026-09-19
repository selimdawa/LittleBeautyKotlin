package com.flatcode.beautytouch.ui.auth

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private var binding: ActivityAuthBinding? = null
    var context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        binding!!.loginBtn.setOnClickListener { context.openActivity<LoginActivity>() }
        binding!!.skipBtn.setOnClickListener { context.openActivity<RegisterActivity>() }
    }
}