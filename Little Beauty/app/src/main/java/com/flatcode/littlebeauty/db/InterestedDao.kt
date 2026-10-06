package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.InterestedEntity
import com.flatcode.littlebeauty.model.ShoppingCenter
import com.flatcode.littlebeauty.model.Tools
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestedDao {

    @Query("SELECT tools.* FROM tools INNER JOIN interested ON CAST(tools.id AS TEXT) = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName")
    fun getInterestedTools(userId: String, databaseName: String): Flow<List<Tools>>

    @Query("SELECT shopping_centers.* FROM shopping_centers INNER JOIN interested ON shopping_centers.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName")
    fun getInterestedShoppingCenters(userId: String, databaseName: String): Flow<List<ShoppingCenter>>

    @Query("SELECT COUNT(*) FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    fun getInterestedCount(userId: String, databaseName: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterested(interested: InterestedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterestedList(list: List<InterestedEntity>)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName AND itemId = :itemId")
    suspend fun deleteInterested(userId: String, databaseName: String, itemId: String)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    suspend fun deleteAllInterestedForUser(userId: String, databaseName: String)
}