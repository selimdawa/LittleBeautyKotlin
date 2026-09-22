package com.flatcode.beautytouch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey var range: String = "",
    var reward: String? = null,
    var reward2: String? = null,
    var reward3: String? = null,
    var reward4: String? = null,
    var reward5: String? = null,
    var reward6: String? = null
) : Parcelable