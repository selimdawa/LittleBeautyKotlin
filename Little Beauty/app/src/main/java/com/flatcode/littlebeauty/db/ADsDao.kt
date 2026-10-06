package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.ADs
import kotlinx.coroutines.flow.Flow

@Dao
interface ADsDao {
    @Query("SELECT * FROM ads")
    fun getAllADs(): Flow<List<ADs>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertADs(ads: List<ADs>)

    @Query("DELETE FROM ads")
    suspend fun deleteAllADs()
}