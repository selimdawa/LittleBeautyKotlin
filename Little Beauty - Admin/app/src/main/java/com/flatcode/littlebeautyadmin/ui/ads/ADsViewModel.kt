package com.flatcode.littlebeautyadmin.ui.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.ADs
import com.flatcode.littlebeautyadmin.model.User
import com.flatcode.littlebeautyadmin.repository.ADsRepository
import com.flatcode.littlebeautyadmin.repository.UserRepository
import com.flatcode.littlebeautyadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ADsViewModel @Inject constructor(
    private val repository: ADsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _ads = MutableStateFlow<List<ADs>>(emptyList())
    val ads: StateFlow<List<ADs>> = _ads

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAds(userId: String, orderBy: String = DATA.NAME) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getAds(userId, orderBy).collect {
                _ads.value = it
                _isLoading.value = false
            }
        }
    }

    fun fetchUsersWithAds(orderBy: String = DATA.AD_LOAD) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getUsersWithAds(orderBy).collect {
                _users.value = it
                _isLoading.value = false
            }
        }
    }

    fun fetchUserInfo(userId: String) {
        viewModelScope.launch {
            userRepository.getUsers().collect { users ->
                _user.value = users.find { it.id == userId }
            }
        }
    }
}
