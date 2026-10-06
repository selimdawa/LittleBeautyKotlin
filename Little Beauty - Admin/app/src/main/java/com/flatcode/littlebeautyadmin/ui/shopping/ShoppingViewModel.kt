package com.flatcode.littlebeautyadmin.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.ShoppingCenter
import com.flatcode.littlebeautyadmin.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(private val repository: ShoppingRepository) : ViewModel() {

    private val _centers = MutableStateFlow<List<ShoppingCenter>>(emptyList())
    val centers: StateFlow<List<ShoppingCenter>> = _centers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    init {
        fetchCenters()
    }

    fun fetchCenters() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getShoppingCenters().collect {
                _centers.value = it
                _isLoading.value = false
            }
        }
    }

    fun deleteCenter(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteShoppingCenter(id)
                _actionStatus.emit(Result.success("Shopping center deleted successfully"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }
}
