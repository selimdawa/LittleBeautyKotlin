package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Main(
    var image: Int = 0, var title: String = "", var number: Int = 0
) : Parcelable
