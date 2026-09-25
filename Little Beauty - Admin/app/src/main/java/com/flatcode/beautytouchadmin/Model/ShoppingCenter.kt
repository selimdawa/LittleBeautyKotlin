@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingCenter(
    var id: String = "",
    var name: String? = null,
    var imageurl: String? = null,
    var imageurl2: String? = null,
    var location: String? = null,
    var location2: String? = null,
    var location3: String? = null,
    var numberPhone: String? = null,
    var publisher: String? = null,
    var aname: String? = null
) : Parcelable
