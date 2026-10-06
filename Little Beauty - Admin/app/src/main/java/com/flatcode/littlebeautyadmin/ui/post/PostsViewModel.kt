package com.flatcode.littlebeautyadmin.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeautyadmin.model.Post
import com.flatcode.littlebeautyadmin.repository.PostRepository
import com.flatcode.littlebeautyadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    fun fetchPosts(type: String = DATA.ALL) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getPosts(type).collect {
                _posts.value = it
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(postId: String, isLiked: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleLike(postId, DATA.FirebaseUserUid, isLiked)
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
                _actionStatus.emit(Result.success("Post deleted successfully"))
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }
}
