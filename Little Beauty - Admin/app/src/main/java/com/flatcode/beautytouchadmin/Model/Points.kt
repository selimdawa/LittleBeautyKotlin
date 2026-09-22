package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "points")
data class Points(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var rewardCount: Int = 0
) : Parcelable