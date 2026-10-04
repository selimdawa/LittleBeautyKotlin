package com.flatcode.beautytouch.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _postsByCategory = MutableStateFlow<List<Post>>(emptyList())
    val postsByCategory: StateFlow<List<Post>> = _postsByCategory

    private val _shoppingCenters = MutableStateFlow<List<ShoppingCenter>>(emptyList())
    val shoppingCenters: StateFlow<List<ShoppingCenter>> = _shoppingCenters

    fun loadPostsByCategory(category: String, publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getPostsByCategory(category, publisher, appName).collect {
                _postsByCategory.value = it
            }
        }
    }

    fun loadShoppingCenters(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getShoppingCenters(publisher, appName).collect {
                _shoppingCenters.value = it
            }
        }
    }

    fun toggleLike(post: Post) {
        repository.toggleLike(post.postid, post.isLiked)
    }

    fun toggleSave(post: Post) {
        repository.toggleSave(post.postid, post.isSaved)
    }
}