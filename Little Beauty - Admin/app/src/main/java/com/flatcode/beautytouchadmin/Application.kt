package com.flatcode.beautytouchadmin

import android.app.Application
import android.text.format.DateFormat
import com.cloudinary.android.MediaManager
import com.flatcode.beautytouchadmin.utils.DATA
import dagger.hilt.android.HiltAndroidApp
import io.selimdawa.multicolors.MultiColorManager
import timber.log.Timber
import java.util.Calendar
import java.util.Locale

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
            "cloud_name" to DATA.CLOUDINARY_CLOUD_NAME, "secure" to true
        )
        MediaManager.init(this, config)
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
    }
}