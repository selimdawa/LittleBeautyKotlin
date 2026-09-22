@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouchadmin.ui.shopping

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouchadmin.model.ShoppingCenter
import com.flatcode.beautytouchadmin.repository.ShoppingRepository
import com.flatcode.beautytouchadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingActionViewModel @Inject constructor(private val repository: ShoppingRepository) : ViewModel() {

    private val _center = MutableStateFlow<ShoppingCenter?>(null)
    val center: StateFlow<ShoppingCenter?> = _center

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCenter(id: String) {
        viewModelScope.launch {
            repository.getShoppingCenter(id).collect {
                _center.value = it
            }
        }
    }

    fun addShoppingCenter(
        name: String, location: String, location2: String, location3: String,
        numberPhone: String, imageUri: Uri, imageUri2: Uri
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val id = repository.generateId() ?: throw Exception("Failed to generate ID")
                val image1 = repository.uploadImage(id, imageUri)
                val image2 = repository.uploadImage(id, imageUri2, "2")

                val hashMap = hashMapOf<String, Any?>(
                    "aname" to DATA.APP_NAME,
                    "id" to id,
                    "imageurl" to image1,
                    "imageurl2" to image2,
                    "location" to location,
                    "location2" to location2,
                    "location3" to location3,
                    "name" to name,
                    "numberPhone" to numberPhone,
                    "timeStamp" to DATA.EMPTY + System.currentTimeMillis(),
                    "publisher" to DATA.EMPTY + DATA.FirebaseUserUid
                )
                repository.addShoppingCenter(hashMap)
                _actionStatus.emit(Result.success("Shopping center added successfully"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateShoppingCenter(
        id: String, name: String, location: String, location2: String, location3: String,
        numberPhone: String, imageUri: Uri?, imageUri2: Uri?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val hashMap = mutableMapOf<String, Any?>(
                    DATA.NAME to name,
                    "location" to location,
                    "location2" to location2,
                    "location3" to location3,
                    "numberPhone" to numberPhone
                )

                if (imageUri != null) {
                    hashMap["imageurl"] = repository.uploadImage(id, imageUri)
                }
                if (imageUri2 != null) {
                    hashMap["imageurl2"] = repository.uploadImage(id, imageUri2, "2")
                }

                repository.updateShoppingCenter(id, hashMap)
                _actionStatus.emit(Result.success("Shopping center updated successfully"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
