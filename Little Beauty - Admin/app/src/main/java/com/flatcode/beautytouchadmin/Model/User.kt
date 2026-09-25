@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var adLoad: Int = 0,
    var adClick: Int = 0,
    var imageurl: String? = null,
    var mversion: String? = null,
    var password: String? = null,
    var phonenumber: String? = null,
    var started: String? = null,
    var username: String? = null
) : Parcelable
