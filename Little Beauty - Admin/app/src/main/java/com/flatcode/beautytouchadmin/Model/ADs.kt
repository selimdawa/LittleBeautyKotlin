package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ADs(
    var name: String = "", var adsLoadedCount: Int = 0, var adsClickedCount: Int = 0
) : Parcelable
