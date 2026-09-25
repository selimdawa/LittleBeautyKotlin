@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouchadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var postid: String = "",
    var category: String? = null,
    var name: String? = null,
    var postimage: String? = null,
    var postimage2: String? = null,
    var postimage3: String? = null,
    var postimage4: String? = null,
    var postimage5: String? = null,
    var postimage6: String? = null,
    var postimage7: String? = null,
    var postimage8: String? = null,
    var postimage9: String? = null,
    var postimage10: String? = null,
    var price: String? = null,
    var indications: String? = null,
    var use: String? = null,
    var publisher: String? = null,
    var aname: String? = null
) : Parcelable
