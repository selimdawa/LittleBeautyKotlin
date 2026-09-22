package com.flatcode.beautytouchadmin.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.repository.PostRepository
import com.flatcode.beautytouchadmin.repository.UserRepository
import com.flatcode.beautytouchadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _savedPosts = MutableStateFlow<List<Post>>(emptyList())
    val savedPosts: StateFlow<List<Post>> = _savedPosts

    fun loadData(userId: String) {
        viewModelScope.launch {
            userRepository.getUser(userId).collect {
                _user.value = it
            }
        }

        viewModelScope.launch {
            combine(
                postRepository.getSavedPostIds(userId),
                postRepository.getAllPosts()
            ) { ids, posts ->
                posts.filter { it.postid in ids && it.publisher == DATA.PUBLISHER && it.aname == DATA.APP_NAME }.reversed()
            }.collect {
                _savedPosts.value = it
            }
        }
    }
}
