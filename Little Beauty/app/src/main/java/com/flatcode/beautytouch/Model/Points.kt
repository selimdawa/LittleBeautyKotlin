package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "points")
data class Points(
    @PrimaryKey var id: String = "",
    var rewardCount: Int = 0,
    var publisher: String? = null,
    var appName: String? = null
) : Parcelable