package com.flatcode.beautytouchadmin.ui.other

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouchadmin.repository.SliderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SliderViewModel @Inject constructor(private val repository: SliderRepository) : ViewModel() {

    private val _sliders = MutableStateFlow<Map<String, String>>(emptyMap())
    val sliders: StateFlow<Map<String, String>> = _sliders

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchSliders()
    }

    private fun fetchSliders() {
        viewModelScope.launch {
            repository.getSliders().collect {
                _sliders.value = it
            }
        }
    }

    fun uploadSlider(name: String, imageUri: Uri) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val imageUrl = repository.uploadSliderImage(name, imageUri)
                repository.updateSlider(mapOf(name to imageUrl))
                _actionStatus.emit(Result.success("Published..."))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
