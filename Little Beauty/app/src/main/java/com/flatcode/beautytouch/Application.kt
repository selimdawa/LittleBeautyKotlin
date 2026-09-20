package com.flatcode.beautytouch

import android.app.Application
import com.cloudinary.android.MediaManager
import com.flatcode.beautytouch.utils.DATA
import dagger.hilt.android.HiltAndroidApp
import io.selimdawa.multicolors.MultiColorManager
import timber.log.Timber

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        MultiColorManager.init(this)

        // Cloudinary Initialization
        val config = mapOf(
            "cloud_name" to DATA.CLOUDINARY_CLOUD_NAME,
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}