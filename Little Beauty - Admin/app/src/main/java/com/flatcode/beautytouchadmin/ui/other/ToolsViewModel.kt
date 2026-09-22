package com.flatcode.beautytouchadmin.ui.other

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouchadmin.model.Tools
import com.flatcode.beautytouchadmin.repository.ToolsRepository
import com.flatcode.beautytouchadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(private val repository: ToolsRepository) : ViewModel() {

    private val _tools = MutableStateFlow<Tools?>(null)
    val tools: StateFlow<Tools?> = _tools

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchTools()
    }

    private fun fetchTools() {
        viewModelScope.launch {
            repository.getTools().collect {
                _tools.value = it
            }
        }
    }

    fun updateTools(
        sessionNow: String, sessionOld: String, sessionNumberNow: String, sessionNumberOld: String,
        yearNow: String, yearOld: String,
        imageUri1: Uri?, imageUri2: Uri?, imageUri3: Uri?, imageUri4: Uri?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val hashMap = mutableMapOf<String, Any?>(
                    "session" to sessionNow,
                    "sessionNumber" to sessionNumberNow,
                    "year" to yearNow,
                    "oldSession" to sessionOld,
                    "oldSessionNumber" to sessionNumberOld,
                    "oldYear" to yearOld
                )

                imageUri1?.let { hashMap["imageSession"] = repository.uploadImage("Images/Session/sessionNow", it) }
                imageUri2?.let { hashMap["oldImageSession"] = repository.uploadImage("Images/Session/sessionOld", it) }
                imageUri3?.let { hashMap["imageLogo"] = repository.uploadImage("Images/Logo/logoNow", it) }
                imageUri4?.let { hashMap["oldImageLogo"] = repository.uploadImage("Images/Logo/logoOld", it) }

                repository.updateTools(hashMap)
                _actionStatus.emit(Result.success("Tools updated successfully"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAboutMe(name: String, imageUri: Uri?) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val hashMap = mutableMapOf<String, Any?>(
                    "aboutMe" to name
                )

                imageUri?.let {
                    hashMap["imageMe"] = repository.uploadImage("Images/AboutMe/" + DATA.FirebaseUserUid, it)
                }

                repository.updateTools(hashMap)
                _actionStatus.emit(Result.success("Modified"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
