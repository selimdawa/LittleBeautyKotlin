package com.flatcode.littlebeauty.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _postDetails = MutableStateFlow<Post?>(null)
    val postDetails: StateFlow<Post?> = _postDetails

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            repository.getPostDetails(postId).collect {
                _postDetails.value = it
            }
        }
        repository.addPostView(postId)
    }

    fun toggleLike(post: Post) {
        repository.toggleLike(post.postid, post.isLiked)
    }

    fun toggleSave(post: Post) {
        repository.toggleSave(post.postid, post.isSaved)
    }
}