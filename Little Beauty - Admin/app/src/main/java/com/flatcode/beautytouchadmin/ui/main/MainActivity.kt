package com.flatcode.beautytouchadmin.ui.main

import android.os.Bundle
import com.flatcode.beautytouchadmin.utils.BaseActivity
import com.flatcode.beautytouchadmin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
    }
}