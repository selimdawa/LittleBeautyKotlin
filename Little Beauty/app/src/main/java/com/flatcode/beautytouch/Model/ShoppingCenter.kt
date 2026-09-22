@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "shopping_centers")
data class ShoppingCenter(
    @PrimaryKey var id: String = "",
    var name: String? = null,
    var imageurl: String? = null,
    var imageurl2: String? = null,
    var location: String? = null,
    var location2: String? = null,
    var location3: String? = null,
    var numberPhone: String? = null,
    var publisher: String? = null,
    var appName: String? = null
) : Parcelable