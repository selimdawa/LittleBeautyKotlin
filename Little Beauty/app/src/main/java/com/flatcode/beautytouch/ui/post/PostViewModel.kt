package com.flatcode.beautytouch.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.repository.PostRepository
import com.flatcode.beautytouch.utils.DATA
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

    private val _favoritePosts = MutableStateFlow<List<Post>>(emptyList())
    val favoritePosts: StateFlow<List<Post>> = _favoritePosts

    private val _skinProducts = MutableStateFlow<List<Post>>(emptyList())
    val skinProducts: StateFlow<List<Post>> = _skinProducts

    private val _hairProducts = MutableStateFlow<List<Post>>(emptyList())
    val hairProducts: StateFlow<List<Post>> = _hairProducts

    private val _shoppingCenters = MutableStateFlow<List<ShoppingCenter>>(emptyList())
    val shoppingCenters: StateFlow<List<ShoppingCenter>> = _shoppingCenters

    fun loadPostsByCategory(category: String, publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getPostsByCategory(category, publisher, appName).collect {
                _postsByCategory.value = it
            }
        }
    }

    fun loadHotProducts(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getHotPosts(publisher, appName).collect {
                _postsByCategory.value = it
            }
        }
    }

    fun loadAllPosts(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getAllPosts(publisher, appName).collect {
                _postsByCategory.value = it
            }
        }
    }

    fun loadFavoritePosts() {
        viewModelScope.launch {
            repository.getFavoritePosts().collect {
                _favoritePosts.value = it
            }
        }
    }

    fun loadSkinProducts(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getPostsByCategory(DATA.SKIN_PRODUCTS, publisher, appName).collect {
                _skinProducts.value = it
            }
        }
    }

    fun loadHairProducts(publisher: String, appName: String) {
        viewModelScope.launch {
            repository.getPostsByCategory(DATA.HAIR_PRODUCTS, publisher, appName).collect {
                _hairProducts.value = it
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