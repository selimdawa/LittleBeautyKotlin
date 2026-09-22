package com.flatcode.beautytouch.db

import androidx.room.*
import com.flatcode.beautytouch.model.Points
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