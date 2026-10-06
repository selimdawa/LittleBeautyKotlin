package com.flatcode.littlebeautyadmin.ui.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.Post
import com.flatcode.littlebeautyadmin.repository.HotProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotProductViewModel @Inject constructor(private val repository: HotProductRepository) : ViewModel() {

    private val _hotPosts = MutableStateFlow<List<Post>>(emptyList())
    val hotPosts: StateFlow<List<Post>> = _hotPosts

    private val _otherPosts = MutableStateFlow<List<Post>>(emptyList())
    val otherPosts: StateFlow<List<Post>> = _otherPosts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    init {
        fetchData()
    }

    private fun fetchData() {
        _isLoading.value = true
        viewModelScope.launch {
            combine(
                repository.getHotProductIds(),
                repository.getPosts()
            ) { ids, posts ->
                val hot = posts.filter { it.postid in ids }.reversed()
                val other = posts.filter { it.postid !in ids }.reversed()
                Pair(hot, other)
            }.collect { (hot, other) ->
                _hotPosts.value = hot
                _otherPosts.value = other
                _isLoading.value = false
            }
        }
    }

    fun addHotProduct(postId: String) {
        viewModelScope.launch {
            try {
                repository.addToHotProducts(postId)
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }

    fun removeHotProduct(postId: String) {
        viewModelScope.launch {
            try {
                repository.removeFromHotProducts(postId)
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }
}
