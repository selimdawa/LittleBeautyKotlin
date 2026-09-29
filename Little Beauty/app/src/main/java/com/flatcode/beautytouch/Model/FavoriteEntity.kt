package com.flatcode.beautytouch.model

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "itemId"])
data class FavoriteEntity(
    val userId: String = "",
    val itemId: String = ""
)
