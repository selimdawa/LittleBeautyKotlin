package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tools")
data class Tools(
    @PrimaryKey var id: Int = 0,
    var imageLogo: String? = null,
    var oldImageLogo: String? = null,
    var imageSession: String? = null,
    var oldImageSession: String? = null,
    var session: String? = null,
    var oldSession: String? = null,
    var sessionNumber: String? = null,
    var oldSessionNumber: String? = null,
    var year: String? = null,
    var oldYear: String? = null,
    var aboutMe: String? = null,
    var imageMe: String? = null
) : Parcelable