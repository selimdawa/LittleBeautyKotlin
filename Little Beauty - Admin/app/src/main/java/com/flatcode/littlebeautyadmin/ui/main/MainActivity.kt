package com.flatcode.littlebeautyadmin.ui.main

import android.os.Bundle
import com.flatcode.littlebeautyadmin.utils.BaseActivity
import com.flatcode.littlebeautyadmin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}