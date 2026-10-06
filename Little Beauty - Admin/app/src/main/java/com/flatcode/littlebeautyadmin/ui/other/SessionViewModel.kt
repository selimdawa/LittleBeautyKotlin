package com.flatcode.littlebeautyadmin.ui.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.User
import com.flatcode.littlebeautyadmin.repository.ToolsRepository
import com.flatcode.littlebeautyadmin.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val toolsRepository: ToolsRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _pointsKey = MutableStateFlow<String?>(null)
    val pointsKey: StateFlow<String?> = _pointsKey

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadSessionInfo(isOld: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            toolsRepository.getTools().collect { tools ->
                tools?.let {
                    val year = if (isOld) it.oldYear else it.year
                    val session = if (isOld) it.oldSession else it.session
                    val key = "${year}_$session"
                    _pointsKey.value = key
                    fetchUsers(key)
                }
            }
        }
    }

    private fun fetchUsers(key: String) {
        viewModelScope.launch {
            userRepository.getUsersOrdered(key, 3).collect {
                _users.value = it
                _isLoading.value = false
            }
        }
    }
}
