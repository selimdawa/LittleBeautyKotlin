package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    var category: String? = null,
    @PrimaryKey
    var id: String = "",
    var imageurl: String? = null,
    var password: String? = null,
    var phonenumber: String? = null,
    var status: String? = null,
    var username: String? = null,
    var city: String? = null,
    var typingTo: String? = null,
    var mversion: String? = null,
    var points: Int = 0
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "", "", "", 0)
}