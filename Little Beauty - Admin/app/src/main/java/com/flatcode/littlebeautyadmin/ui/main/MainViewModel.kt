package com.flatcode.littlebeautyadmin.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.User
import com.flatcode.littlebeautyadmin.repository.MainRepository
import com.flatcode.littlebeautyadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MainState(
    val usersCount: Int = 0,
    val hotProductsCount: Int = 0,
    val postsCount: Int = 0,
    val shoppingCentersCount: Int = 0,
    val sliderShowCount: Int = 0,
    val user: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    init {
        fetchData()
    }

    private fun fetchData() {
        val userId = DATA.FirebaseUserUid
        viewModelScope.launch {
            combine(
                repository.getUsersCount(),
                repository.getHotProductsCount(),
                repository.getPostsCount(userId),
                repository.getShoppingCentersCount(userId),
                repository.getSliderShowCount(),
                repository.getUserInfo(userId)
            ) { array ->
                MainState(
                    usersCount = array[0] as Int,
                    hotProductsCount = array[1] as Int,
                    postsCount = array[2] as Int,
                    shoppingCentersCount = array[3] as Int,
                    sliderShowCount = array[4] as Int,
                    user = array[5] as User?,
                    isLoading = false
                )
            }.collect { newState ->
                Timber.d("Main data fetched successfully: $newState")
                _state.value = newState
            }
        }
    }
}
