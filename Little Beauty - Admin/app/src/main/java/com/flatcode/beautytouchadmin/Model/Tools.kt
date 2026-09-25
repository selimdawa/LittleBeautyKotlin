package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tools(
    var session: String = "",
    var imageLogo: String? = null,
    var oldImageLogo: String? = null,
    var imageSession: String? = null,
    var oldImageSession: String? = null,
    var oldSession: String? = null,
    var sessionNumber: String? = null,
    var oldSessionNumber: String? = null,
    var year: String? = null,
    var oldYear: String? = null,
    var aboutMe: String? = null,
    var imageMe: String? = null
) : Parcelable
