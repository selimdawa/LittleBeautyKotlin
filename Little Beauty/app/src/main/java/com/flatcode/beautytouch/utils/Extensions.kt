package com.flatcode.beautytouch.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.model.ADs
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.io.Serializable

inline fun <reified T : Activity> Context.openActivity(
    vararg extras: Pair<String, Any?>,
    clear: Boolean = false
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

fun ImageView.Glide(isUser: Boolean, context: Context?, Url: String?) {
    try {
        if (Url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_user)
            }
        } else {
            this.load(Url) {
                placeholder(R.color.image_profile)
                crossfade(true)
            }
        }
    } catch (e: Exception) {
        this.setImageResource(R.drawable.basic_user)
    }
}

fun ImageView.GlideBlur(isUser: Boolean, context: Context?, Url: String?, level: Int) {
    try {
        if (Url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_user)
            }
        } else {
            this.load(Url) {
                placeholder(R.color.image_profile)
                transformations(SimpleBlurTransformation(level.toFloat()))
            }
        }
    } catch (e: Exception) {
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

fun AdView.BannerAd(context: Context?, bannerName: String?) {
    if (context != null) MobileAds.initialize(context) { }

    val adRequest = AdRequest.Builder().build()
    this.loadAd(adRequest)
    this.adListener = object : AdListener() {
        override fun onAdLoaded() {
            DATA.FirebaseUserUid.AdUserCount(DATA.AD_LOAD, 1)
            DATA.FirebaseUserUid.AdCount(bannerName, DATA.ADS_LOADED_COUNT)
        }

        override fun onAdOpened() {
            DATA.FirebaseUserUid.AdUserCount(DATA.AD_CLICK, 1)
            DATA.FirebaseUserUid.AdCount(bannerName, DATA.ADS_CLICKED_COUNT)
        }
    }
}

fun Activity.InterstitialAd() {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        this, this.resources.getString(R.string.admob_interstitial), adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                mInterstitialAd = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                mInterstitialAd = null
            }
        })
}

fun Activity.InterstitialShow(interstitialName: String?) {
    if (mInterstitialAd != null) {
        mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                mInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                mInterstitialAd = null
                this@InterstitialShow.InterstitialAd()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                DATA.FirebaseUserUid.AdUserCount(DATA.AD_LOAD, 1)
                DATA.FirebaseUserUid.AdCount(interstitialName, DATA.ADS_LOADED_COUNT)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                DATA.FirebaseUserUid.AdUserCount(DATA.AD_CLICK, 1)
                DATA.FirebaseUserUid.AdCount(interstitialName, DATA.ADS_CLICKED_COUNT)
            }
        }
        mInterstitialAd!!.show(this)
    }
}

fun String?.AdCount(bannerName: String?, key: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this!!)
    ref.child(bannerName!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var adCount = DATA.EMPTY + snapshot.child(key!!).value
            if (adCount == DATA.EMPTY || adCount == DATA.NULL) {
                adCount = "0"
            }
            val newAdCount = adCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[key] = newAdCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this@AdCount)
            reference.child(bannerName).updateChildren(hashMap).addOnCompleteListener {
                this@AdCount.AdName(bannerName)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.AdRewardCount(key: String) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        .child(this!!)
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var adCount = DATA.EMPTY + snapshot.child(key).value
            if (adCount == DATA.EMPTY || adCount == DATA.NULL) {
                adCount = "0"
            }
            val newAdCount = adCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[key] = newAdCount
            ref.updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.AdUserCount(key: String?, number: Int) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(
        this!!
    )
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var adCount = DATA.EMPTY + snapshot.child(key!!).value
            if (adCount == DATA.EMPTY || adCount == DATA.NULL) {
                adCount = "0"
            }
            val newAdCount = adCount.toLong() + number
            val hashMap = HashMap<String?, Any>()
            hashMap[key] = newAdCount
            val ref2 = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(this@AdUserCount)
            ref2.updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.AdName(bannerName: String?) {
    val ref = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this!!)
    ref.child(bannerName!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val item = snapshot.getValue(ADs::class.java)!!
            if (item.name == null) {
                val hashMap = HashMap<String?, Any?>()
                hashMap[DATA.NAME] = bannerName
                val ref2 = FirebaseDatabase.getInstance().getReference(DATA.M_AD).child(this@AdName)
                ref2.child(bannerName).updateChildren(hashMap)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun Activity.RateUs() {
    val uri = Uri.parse("market://details?id=" + this.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        this.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
            )
        )
    }
}

fun Uri?.getFileExtension(context: Context): String? {
    val cR = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this!!))
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