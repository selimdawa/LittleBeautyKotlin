package com.flatcode.littlebeauty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hot_products")
data class HotProductEntity(
    @PrimaryKey var id: String = ""
)