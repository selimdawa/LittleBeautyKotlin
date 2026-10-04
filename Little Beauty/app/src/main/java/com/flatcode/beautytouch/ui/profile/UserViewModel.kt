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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository, private val auth: FirebaseAuth
) : ViewModel() {

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo

    private val _appTools = MutableStateFlow<Tools?>(null)
    val appTools: StateFlow<Tools?> = _appTools

    private val _updateProfileState = MutableStateFlow<Result<Unit>?>(null)
    val updateProfileState: StateFlow<Result<Unit>?> = _updateProfileState

    private val _points = MutableStateFlow<String>("0")
    val points: StateFlow<String> = _points

    private val _leaderboard = MutableStateFlow<List<User>>(emptyList())
    val leaderboard: StateFlow<List<User>> = _leaderboard

    private val _rewards = MutableStateFlow<Reward?>(null)
    val rewards: StateFlow<Reward?> = _rewards

    fun loadUserInfo() {
        viewModelScope.launch {
            repository.getUserInfo().collect {
                _userInfo.value = it
            }
        }
    }

    fun loadAppTools() {
        viewModelScope.launch {
            repository.getAppTools().collect {
                _appTools.value = it
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun updateProfile(username: String, imageUrl: String? = null) {
        viewModelScope.launch {
            val result = repository.updateProfile(username, imageUrl)
            _updateProfileState.value = result
        }
    }

    fun uploadProfileImageCloudinary(imageUri: Uri, onComplete: (String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val publicId = "${uid}_${System.currentTimeMillis()}"

        MediaManager.get().upload(imageUri).option("public_id", publicId)
            .option("folder", "Images/Profile").unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as? String ?: ""
                    onComplete(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onComplete(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    fun loadPoints(year: String, session: String) {
        viewModelScope.launch {
            repository.getPoints(year, session).collect {
                _points.value = it
            }
        }
    }

    fun addRewardPoint(year: String, session: String) {
        repository.addRewardPoint(year, session)
    }

    fun loadLeaderboard(orderBy: String, limit: Int = 3) {
        viewModelScope.launch {
            repository.getLeaderboard(orderBy, limit).collect {
                _leaderboard.value = it
            }
        }
    }

    fun loadRewards() {
        viewModelScope.launch {
            repository.getRewards().collect {
                _rewards.value = it
            }
        }
    }
}
