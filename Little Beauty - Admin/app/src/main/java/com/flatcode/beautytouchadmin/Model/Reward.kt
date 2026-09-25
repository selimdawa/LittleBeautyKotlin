package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reward(
    var range: String = "",
    var reward: String? = null,
    var reward2: String? = null,
    var reward3: String? = null,
    var reward4: String? = null,
    var reward5: String? = null,
    var reward6: String? = null
) : Parcelable
