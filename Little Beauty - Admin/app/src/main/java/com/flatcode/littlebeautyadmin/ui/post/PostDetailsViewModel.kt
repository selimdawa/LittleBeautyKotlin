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
class PostDetailsViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _likesCount = MutableStateFlow(0L)
    val likesCount: StateFlow<Long> = _likesCount

    private val _actionStatus = MutableSharedFlow<Result<String>>()
    val actionStatus: SharedFlow<Result<String>> = _actionStatus

    fun loadPost(postId: String) {
        viewModelScope.launch {
            repository.getPost(postId).collect {
                _post.value = it
            }
        }
        
        viewModelScope.launch {
            repository.isLiked(postId, DATA.FirebaseUserUid).collect {
                _isLiked.value = it
            }
        }

        viewModelScope.launch {
            repository.isSaved(postId, DATA.FirebaseUserUid).collect {
                _isSaved.value = it
            }
        }

        viewModelScope.launch {
            repository.getLikesCount(postId).collect {
                _likesCount.value = it
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                repository.toggleLike(postId, DATA.FirebaseUserUid, _isLiked.value)
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            try {
                repository.toggleSave(postId, DATA.FirebaseUserUid, _isSaved.value)
            } catch (e: Exception) {
                _actionStatus.emit(Result.failure(e))
            }
        }
    }
}
