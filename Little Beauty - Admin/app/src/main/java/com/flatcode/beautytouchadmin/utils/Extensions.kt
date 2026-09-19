package com.flatcode.beautytouchadmin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.beautytouchadmin.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

fun Context.intentClear(c: Class<*>?) {
    val intent = Intent(this, c)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

fun Context.intent1(c: Class<*>?) {
    val intent = Intent(this, c)
    this.startActivity(intent)
}

fun Context.intentExtra(c: Class<*>?, key: String?, value: String?) {
    val intent = Intent(this, c)
    intent.putExtra(key, value)
    this.startActivity(intent)
}

fun NavController.navigateAction(actionId: Int) {
    this.navigate(actionId)
}

fun NavController.navigateWithBundle(actionId: Int, bundle: Bundle) {
    this.navigate(actionId, bundle)
}

fun Context.intentExtra2(
    c: Class<*>?, key: String?, value: String?, key2: String?, value2: String?
) {
    val intent = Intent(this, c)
    intent.putExtra(key, value)
    intent.putExtra(key2, value2)
    this.startActivity(intent)
}

fun Context.intentExtra3(
    c: Class<*>?, key: String?, value: String?,
    key2: String?, value2: String?, key3: String?, value3: String?
) {
    val intent = Intent(this, c)
    intent.putExtra(key, value)
    intent.putExtra(key2, value2)
    intent.putExtra(key3, value3)
    this.startActivity(intent)
}

fun ImageView.glide(isUser: Boolean, url: String?) {
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
    } catch (e: Exception) {
        this.setImageResource(R.drawable.icon)
    }
}

fun ImageView.glideBlur(isUser: Boolean, url: String?, level: Int) {
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
                transformations(SimpleBlurTransformation(level.toFloat()))
            }
        }
    } catch (e: Exception) {
        this.setImageResource(R.drawable.icon)
    }
}

fun Activity.cropImageSquare() {
    CropImage.activity()
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMultiTouchEnabled(true)
        .setMinCropResultSize(DATA.MIN_SQUARE, DATA.MIN_SQUARE)
        .setAspectRatio(1, 1)
        .setCropShape(CropImageView.CropShape.OVAL)
        .start(this)
}

fun Activity.cropImageSlider() {
    CropImage.activity()
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMultiTouchEnabled(true)
        .setMinCropResultSize(DATA.MIX_SLIDER_X, DATA.MIX_SLIDER_Y)
        .setAspectRatio(16, 9)
        .setCropShape(CropImageView.CropShape.OVAL)
        .start(this)
}

fun Activity.cropImageShoppingCenter() {
    CropImage.activity()
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMultiTouchEnabled(true)
        .setMinCropResultSize(DATA.MIX_SLIDER_X, DATA.MIX_SLIDER_Y)
        .setAspectRatio(2, 1)
        .setCropShape(CropImageView.CropShape.OVAL)
        .start(this)
}

fun Activity.cropImageSession() {
    CropImage.activity()
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMultiTouchEnabled(true)
        .setMinCropResultSize(DATA.MIN_SQUARE, DATA.MIN_SQUARE)
        .setAspectRatio(1, 1)
        .setCropShape(CropImageView.CropShape.OVAL)
        .start(this)
}

fun Uri.getFileExtension(context: Context): String? {
    val cR = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this))
}

fun View.applySystemBarsPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(v.paddingLeft, insets.top, v.paddingRight, insets.bottom)
        windowInsets
    }
}

fun View.applyStatusBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(v.paddingLeft, insets.top, v.paddingRight, v.paddingBottom)
        windowInsets
    }
}

fun View.applyNavigationBarPadding() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, insets.bottom)
        windowInsets
    }
}

class SimpleBlurTransformation(private val radius: Float) : Transformation() {
    override val cacheKey: String = "${SimpleBlurTransformation::class.java.name}-$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        if (input.isRecycled) return input
        val scaleFactor = 6
        val w = (input.width / scaleFactor).coerceAtLeast(1)
        val h = (input.height / scaleFactor).coerceAtLeast(1)
        val small = input.scale(w, h, true)
        val r = (radius / scaleFactor).toInt().coerceAtLeast(1)
        val pix = IntArray(w * h)
        small.getPixels(pix, 0, w, 0, 0, w, h)
        val blurred = IntArray(w * h)
        for (y in 0 until h) for (x in 0 until w) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val xi = (x + i).coerceIn(0, w - 1)
                val p = pix[y * w + xi]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            blurred[y * w + x] = (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        for (x in 0 until w) for (y in 0 until h) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val yi = (y + i).coerceIn(0, h - 1)
                val p = blurred[yi * w + x]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            pix[y * w + x] = (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(pix, 0, w, 0, 0, w, h)
        val finalOutput = output.scale(input.width, input.height, true)
        if (output != finalOutput) output.recycle()
        if (small != input) small.recycle()
        return finalOutput
    }
}
