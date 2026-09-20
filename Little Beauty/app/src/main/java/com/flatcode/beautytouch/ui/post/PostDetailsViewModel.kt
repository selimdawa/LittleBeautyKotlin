package com.flatcode.beautytouch.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.repository.PostRepository
import com.flatcode.beautytouch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _postDetails = MutableStateFlow<Resource<Post>?>(null)
    val postDetails: StateFlow<Resource<Post>?> = _postDetails

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