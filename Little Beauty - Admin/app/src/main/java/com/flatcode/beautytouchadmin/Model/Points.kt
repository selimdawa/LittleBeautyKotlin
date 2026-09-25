package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Points(
        var id: Int = 0,
    var rewardCount: Int = 0
) : Parcelable
