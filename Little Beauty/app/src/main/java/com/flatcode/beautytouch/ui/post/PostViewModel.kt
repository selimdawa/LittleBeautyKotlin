package com.flatcode.beautytouch.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.repository.PostRepository
import com.flatcode.beautytouch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _postsByCategory = MutableStateFlow<Resource<List<Post>>?>(null)
    val postsByCategory: StateFlow<Resource<List<Post>>?> = _postsByCategory

    private val _skinCount = MutableStateFlow<Resource<Int>?>(null)
    val skinCount: StateFlow<Resource<Int>?> = _skinCount

    private val _hairCount = MutableStateFlow<Resource<Int>?>(null)
    val hairCount: StateFlow<Resource<Int>?> = _hairCount

    private val _shoppingCount = MutableStateFlow<Resource<Int>?>(null)
    val shoppingCount: StateFlow<Resource<Int>?> = _shoppingCount

    private val _shoppingCenters = MutableStateFlow<Resource<List<ShoppingCenter>>?>(null)
    val shoppingCenters: StateFlow<Resource<List<ShoppingCenter>>?> = _shoppingCenters

    private val _favoritePosts = MutableStateFlow<Resource<List<Post>>?>(null)
    val favoritePosts: StateFlow<Resource<List<Post>>?> = _favoritePosts

    fun loadPostsByCategory(category: String, publisher: String, aname: String) {
        Timber.d("Loading posts for category: $category, publisher: $publisher")
        viewModelScope.launch {
            repository.getPostsByCategory(category, publisher, aname).collect {
                _postsByCategory.value = it
                Timber.d("Posts by category state updated: $it")
            }
        }
    }

    fun loadCategoryCounts(publisher: String, aname: String, skinCat: String, hairCat: String, shoppingCat: String) {
        Timber.d("Loading category counts for publisher: $publisher")
        viewModelScope.launch {
            repository.getCategoryCount(skinCat, publisher, aname).collect {
                _skinCount.value = it
                Timber.d("Skin count state updated: $it")
            }
        }
        viewModelScope.launch {
            repository.getCategoryCount(hairCat, publisher, aname).collect {
                _hairCount.value = it
                Timber.d("Hair count state updated: $it")
            }
        }
        viewModelScope.launch {
            repository.getCategoryCount(shoppingCat, publisher, aname).collect {
                _shoppingCount.value = it
                Timber.d("Shopping count state updated: $it")
            }
        }
    }

    fun loadShoppingCenters(publisher: String, aname: String) {
        Timber.d("Loading shopping centers for publisher: $publisher")
        viewModelScope.launch {
            repository.getShoppingCenters(publisher, aname).collect {
                _shoppingCenters.value = it
                Timber.d("Shopping centers state updated: $it")
            }
        }
    }

    fun loadFavoritePosts(publisher: String, aname: String) {
        Timber.d("Loading favorite posts for publisher: $publisher")
        viewModelScope.launch {
            repository.getFavoritePosts(publisher, aname).collect {
                _favoritePosts.value = it
                Timber.d("Favorite posts state updated: $it")
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