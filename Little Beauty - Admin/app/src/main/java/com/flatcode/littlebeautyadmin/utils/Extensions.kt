package com.flatcode.littlebeautyadmin.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.flatcode.littlebeautyadmin.R

inline fun <reified T : Activity> Context.openActivity(
    vararg extras: Pair<String, Any?>, clear: Boolean = false
) {
    val intent = Intent(this, T::class.java).apply {
        if (clear) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (extras.isNotEmpty()) {
            val bundle = Bundle()
            extras.forEach { pair ->
                val key = pair.first
                when (val value = pair.second) {
                    is String -> bundle.putString(key, value)
                    is Int -> bundle.putInt(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                    is Long -> bundle.putLong(key, value)
                    is Double -> bundle.putDouble(key, value)
                    is Float -> bundle.putFloat(key, value)
                    else -> bundle.putString(key, value?.toString())
                }
            }
            putExtras(bundle)
        }
    }
    startActivity(intent)
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            ?: return false
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Activity.pickImage(requestCode: Int) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
}

fun Activity.cropImage(
    uri: Uri,
    aspectRatioX: Int = 1,
    aspectRatioY: Int = 1,
    isOval: Boolean = false,
    minWidth: Int = DATA.MIN_SQUARE,
    minHeight: Int = DATA.MIN_SQUARE,
    requestCode: Int = DATA.MIN_SQUARE
) {
    val intent = Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", aspectRatioX)
        putExtra("ASPECT_RATIO_Y", aspectRatioY)
        putExtra("IS_OVAL", isOval)
        putExtra("MIN_WIDTH", minWidth)
        putExtra("MIN_HEIGHT", minHeight)
    }
    startActivityForResult(intent, requestCode)
}

fun ImageView.loadImage(isUser: Boolean, data: Any? = null, url: Any? = null) {
    val imageSource = data ?: url
    val defaultRes = if (isUser) R.drawable.basic_user else R.color.image_profile
    try {
        val str = imageSource?.toString()
        if (imageSource == null || str.isNullOrBlank() || str == DATA.BASIC || str == "null") {
            this.setImageResource(defaultRes)
        } else {
            this.load(imageSource) {
                placeholder(R.color.image_profile)
                error(defaultRes)
                fallback(defaultRes)
                crossfade(true)
            }
        }
    } catch (_: Exception) {
        this.setImageResource(defaultRes)
    }
}

fun Activity.checkStoragePermission(
    launcher: ActivityResultLauncher<String>, onGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onGranted()
    } else {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            launcher.launch(permission)
        }
    }
}
