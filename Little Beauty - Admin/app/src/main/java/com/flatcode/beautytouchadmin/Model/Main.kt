package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "main_menu")
data class Main(
    var image: Int = 0, @PrimaryKey var title: String = "", var number: Int = 0
) : Parcelable