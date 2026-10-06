package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.Points
import kotlinx.coroutines.flow.Flow

@Dao
interface PointsDao {
    @Query("SELECT * FROM points WHERE publisher = :publisher AND appName = :appName")
    fun getPoints(publisher: String, appName: String): Flow<Points?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: Points)

    @Query("DELETE FROM points")
    suspend fun deleteAllPoints()
}