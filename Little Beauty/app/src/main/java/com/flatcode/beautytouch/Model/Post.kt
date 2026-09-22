package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "posts")
data class Post(
    var category: String? = null,
    var name: String? = null,
    @PrimaryKey
    var postid: String = "",
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
    var appName: String? = null,
    var isLiked: Boolean = false,
    var isSaved: Boolean = false,
    var nrLikes: Int = 0
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", false, false, 0)
}