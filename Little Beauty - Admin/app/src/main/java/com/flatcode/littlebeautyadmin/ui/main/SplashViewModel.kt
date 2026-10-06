package com.flatcode.littlebeautyadmin.ui.main

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
