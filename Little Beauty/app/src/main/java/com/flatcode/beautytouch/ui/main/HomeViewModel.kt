package com.flatcode.beautytouch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _hotProducts = MutableStateFlow<List<Post>>(emptyList())
    val hotProducts: StateFlow<List<Post>> = _hotProducts

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts

    private val _sliderImages = MutableStateFlow<List<String>>(emptyList())
    val sliderImages: StateFlow<List<String>> = _sliderImages

    fun loadHomeData(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getHotPosts(publisher, appName).collect {
                _hotProducts.value = it
            }
        }
        viewModelScope.launch {
            repository.getAllPosts(publisher, appName).collect {
                _allPosts.value = it
            }
        }
        viewModelScope.launch {
            repository.getImageSliderUrls().collect {
                _sliderImages.value = it
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