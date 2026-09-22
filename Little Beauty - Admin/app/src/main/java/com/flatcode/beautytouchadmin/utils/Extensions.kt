package com.flatcode.beautytouchadmin.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.flatcode.beautytouchadmin.R
import java.io.Serializable

inline fun <reified T : Activity> Context.openActivity(
    vararg extras: Pair<String, Any?>, clear: Boolean = false
) {
    val intent = Intent(this, T::class.java).apply {
        if (clear) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Serializable -> putExtra(key, value)
            }
        }
    }
    startActivity(intent)
}

fun ImageView.loadImage(isUser: Boolean, url: String?) {
    try {
        if (url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.icon)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                crossfade(true)
            }
        }
    } catch (_: Exception) {
        this.setImageResource(R.drawable.icon)
    }
}

fun cropImageSquareOptions(): CropImageContractOptions = CropImageContractOptions(
    uri = null, cropImageOptions = CropImageOptions(
        guidelines = CropImageView.Guidelines.ON,
        multiTouchEnabled = true,
        minCropResultWidth = DATA.MIN_SQUARE,
        minCropResultHeight = DATA.MIN_SQUARE,
        aspectRatioX = 1,
        aspectRatioY = 1,
        fixAspectRatio = true,
        cropShape = CropImageView.CropShape.OVAL
    )
)

fun cropImageSliderOptions(): CropImageContractOptions = CropImageContractOptions(
    uri = null, cropImageOptions = CropImageOptions(
        guidelines = CropImageView.Guidelines.ON,
        multiTouchEnabled = true,
        minCropResultWidth = DATA.MIN_SLIDER_X,
        minCropResultHeight = DATA.MIN_SLIDER_Y,
        aspectRatioX = 16,
        aspectRatioY = 9,
        fixAspectRatio = true,
        cropShape = CropImageView.CropShape.OVAL
    )
)

fun cropImageShoppingCenterOptions(): CropImageContractOptions = CropImageContractOptions(
    uri = null, cropImageOptions = CropImageOptions(
        guidelines = CropImageView.Guidelines.ON,
        multiTouchEnabled = true,
        minCropResultWidth = DATA.MIN_SLIDER_X,
        minCropResultHeight = DATA.MIN_SLIDER_Y,
        aspectRatioX = 2,
        aspectRatioY = 1,
        fixAspectRatio = true,
        cropShape = CropImageView.CropShape.OVAL
    )
)

fun cropImageSessionOptions(): CropImageContractOptions = CropImageContractOptions(
    uri = null, cropImageOptions = CropImageOptions(
        guidelines = CropImageView.Guidelines.ON,
        multiTouchEnabled = true,
        minCropResultWidth = DATA.MIN_SQUARE,
        minCropResultHeight = DATA.MIN_SQUARE,
        aspectRatioX = 1,
        aspectRatioY = 1,
        fixAspectRatio = true,
        cropShape = CropImageView.CropShape.OVAL
    )
)

fun Activity.checkStoragePermission(
    launcher: ActivityResultLauncher<String>, onGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // On Android 13+ (API 33+) and Android 14+ (API 34+), storage permissions are not required
        // to pick images using the system Photo Picker / standard GET_CONTENT intent.
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

fun Uri.getFileExtension(context: Context): String? {
    val contentResolver = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(contentResolver.getType(this))
}