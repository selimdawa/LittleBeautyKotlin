package com.flatcode.beautytouch.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.flatcode.beautytouch.model.Reward
import com.flatcode.beautytouch.model.Tools
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.repository.UserRepository
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userInfo = MutableStateFlow<Resource<User>?>(null)
    val userInfo: StateFlow<Resource<User>?> = _userInfo

    private val _appTools = MutableStateFlow<Resource<Tools>?>(null)
    val appTools: StateFlow<Resource<Tools>?> = _appTools

    private val _updateProfileState = MutableStateFlow<Resource<Boolean>?>(null)
    val updateProfileState: StateFlow<Resource<Boolean>?> = _updateProfileState

    private val _uploadImageState = MutableStateFlow<Resource<String>?>(null)
    val uploadImageState: StateFlow<Resource<String>?> = _uploadImageState

    private val _points = MutableStateFlow<Resource<String>?>(null)
    val points: StateFlow<Resource<String>?> = _points

    private val _leaderboard = MutableStateFlow<Resource<List<User>>?>(null)
    val leaderboard: StateFlow<Resource<List<User>>?> = _leaderboard

    private val _rewards = MutableStateFlow<Resource<Reward>?>(null)
    val rewards: StateFlow<Resource<Reward>?> = _rewards

    fun loadUserInfo() {
        Timber.d("Loading user info")
        viewModelScope.launch {
            repository.getUserInfo().collect {
                _userInfo.value = it
                Timber.d("User info state updated: $it")
            }
        }
    }

    fun loadAppTools() {
        Timber.d("Loading app tools")
        viewModelScope.launch {
            repository.getAppTools().collect {
                _appTools.value = it
                Timber.d("App tools state updated: $it")
            }
        }
    }

    fun logout() {
        Timber.d("User logout requested")
        repository.logout()
    }

    fun updateProfile(username: String, imageUrl: String? = null) {
        Timber.d("Updating profile for username: $username")
        viewModelScope.launch {
            repository.updateProfile(username, imageUrl).collect {
                _updateProfileState.value = it
                Timber.d("Update profile state updated: $it")
            }
        }
    }

    fun uploadProfileImageCloudinary(imageUri: Uri, onComplete: (String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        _uploadImageState.value = Resource.Loading
        val publicId = "${uid}_${System.currentTimeMillis()}"

        MediaManager.get().upload(imageUri)
            .option("public_id", publicId)
            .option("folder", "Images/Profile")
            .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Timber.d("Cloudinary upload started: $requestId")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String ?: ""
                    Timber.d("Cloudinary upload success: $imageUrl")
                    _uploadImageState.value = Resource.Success(imageUrl)
                    onComplete(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    val message = error?.description ?: "Unknown error"
                    Timber.e("Cloudinary upload error: $message")
                    _uploadImageState.value = Resource.Error(message)
                    onComplete(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    fun loadPoints(year: String, session: String) {
        Timber.d("Loading points for year: $year, session: $session")
        viewModelScope.launch {
            repository.getPoints(year, session).collect {
                _points.value = it
                Timber.d("Points state updated: $it")
            }
        }
    }

    fun addRewardPoint(year: String, session: String) {
        Timber.d("Adding reward point for year: $year, session: $session")
        repository.addRewardPoint(year, session)
    }

    fun loadLeaderboard(orderBy: String, limit: Int = 3) {
        Timber.d("Loading leaderboard ordered by: $orderBy, limit: $limit")
        viewModelScope.launch {
            repository.getLeaderboard(orderBy, limit).collect {
                _leaderboard.value = it
                Timber.d("Leaderboard state updated: $it")
            }
        }
    }

    fun loadRewards() {
        Timber.d("Loading rewards")
        viewModelScope.launch {
            repository.getRewards().collect {
                _rewards.value = it
                Timber.d("Rewards state updated: $it")
            }
        }
    }
}
