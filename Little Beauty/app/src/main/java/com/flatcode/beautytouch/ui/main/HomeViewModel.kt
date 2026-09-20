package com.flatcode.beautytouch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.repository.PostRepository
import com.flatcode.beautytouch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _hotProducts = MutableStateFlow<Resource<List<Post>>?>(null)
    val hotProducts: StateFlow<Resource<List<Post>>?> = _hotProducts

    private val _allPosts = MutableStateFlow<Resource<List<Post>>?>(null)
    val allPosts: StateFlow<Resource<List<Post>>?> = _allPosts

    private val _sliderImages = MutableStateFlow<Resource<List<String>>?>(null)
    val sliderImages: StateFlow<Resource<List<String>>?> = _sliderImages

    fun loadHomeData(publisher: String, aname: String) {
        Timber.d("Loading home data for publisher: $publisher, aname: $aname")
        viewModelScope.launch {
            repository.getHotProducts(publisher, aname).collect {
                _hotProducts.value = it
                Timber.d("Hot products state updated: $it")
            }
        }
        viewModelScope.launch {
            repository.getAllPosts(publisher, aname).collect {
                _allPosts.value = it
                Timber.d("All posts state updated: $it")
            }
        }
        viewModelScope.launch {
            repository.getImageSliderUrls().collect {
                _sliderImages.value = it
                Timber.d("Slider images state updated: $it")
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