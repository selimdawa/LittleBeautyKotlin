package com.flatcode.beautytouch.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.ui.main.MainActivity.Companion.mInterstitialAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
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
                is Parcelable -> putExtra(key, value)
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
                this.setImageResource(R.drawable.basic_user)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                crossfade(true)
            }
        }
    } catch (_: Exception) {
        this.setImageResource(R.drawable.basic_user)
    }
}

fun Context.startCropActivity(
    uri: Uri, aspectRatioX: Int = 1, aspectRatioY: Int = 1, isOval: Boolean = false
): Intent {
    return Intent(this, CropActivity::class.java).apply {
        putExtra("IMAGE_URI", uri)
        putExtra("ASPECT_RATIO_X", aspectRatioX)
        putExtra("ASPECT_RATIO_Y", aspectRatioY)
        putExtra("IS_OVAL", isOval)
        putExtra("MIN_WIDTH", DATA.MIN_SQUARE)
        putExtra("MIN_HEIGHT", DATA.MIN_SQUARE)
    }
}

fun AdView.bannerAd(context: Context?, bannerName: String?) {
    if (context != null) MobileAds.initialize(context) { }

    val adRequest = AdRequest.Builder().build()
    this.loadAd(adRequest)
    this.adListener = object : AdListener() {
        override fun onAdLoaded() {
            FirebaseAuth.getInstance().currentUser?.uid.adUserCount(DATA.AD_LOAD, 1)
            FirebaseAuth.getInstance().currentUser?.uid.adCount(bannerName, DATA.ADS_LOADED_COUNT)
        }

        override fun onAdOpened() {
            FirebaseAuth.getInstance().currentUser?.uid.adUserCount(DATA.AD_CLICK, 1)
            FirebaseAuth.getInstance().currentUser?.uid.adCount(bannerName, DATA.ADS_CLICKED_COUNT)
        }
    }
}

fun Activity.interstitialAd() {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        this,
        this.resources.getString(R.string.admob_interstitial),
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                mInterstitialAd = null
            }
        })
}

fun Activity.interstitialShow(interstitialName: String?) {
    mInterstitialAd?.let { ad ->
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                this@interstitialShow.interstitialAd()
            }

            override fun onAdImpression() {
                FirebaseAuth.getInstance().currentUser?.uid.adUserCount(DATA.AD_LOAD, 1)
                FirebaseAuth.getInstance().currentUser?.uid.adCount(interstitialName, DATA.ADS_LOADED_COUNT)
            }

            override fun onAdClicked() {
                FirebaseAuth.getInstance().currentUser?.uid.adUserCount(DATA.AD_CLICK, 1)
                FirebaseAuth.getInstance().currentUser?.uid.adCount(interstitialName, DATA.ADS_CLICKED_COUNT)
            }
        }
        ad.show(this)
    }
}

class SimpleBlurTransformation(private val radius: Float) : Transformation() {
    override val cacheKey: String = "${SimpleBlurTransformation::class.qualifiedName}-$radius"

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
            blurred[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
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
            pix[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(pix, 0, w, 0, 0, w, h)
        val finalOutput = output.scale(input.width, input.height, true)
        if (output != finalOutput) output.recycle()
        if (small != input) small.recycle()
        return finalOutput
    }
}