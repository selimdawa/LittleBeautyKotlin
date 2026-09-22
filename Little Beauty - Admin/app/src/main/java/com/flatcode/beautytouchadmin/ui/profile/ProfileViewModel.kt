package com.flatcode.beautytouchadmin.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.repository.UserRepository
import com.flatcode.beautytouchadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadUserInfo(userId: String) {
        viewModelScope.launch {
            repository.getUser(userId).collect {
                _user.value = it
            }
        }
    }

    fun updateProfile(userId: String, username: String, imageUri: Uri?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val hashMap = mutableMapOf<String, Any?>(
                    DATA.USER_NAME to username
                )

                if (imageUri != null) {
                    val imageUrl = repository.uploadProfileImage(userId, imageUri)
                    hashMap[DATA.IMAGE_URL] = imageUrl
                }

                repository.updateProfile(userId, hashMap)
                _actionStatus.emit(Result.success("Modifications are loaded..."))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
