package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.HotProductEntity
import com.flatcode.littlebeauty.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface HotProductDao {

    @Query("SELECT posts.* FROM posts INNER JOIN hot_products ON posts.postid = hot_products.id ORDER BY posts.postid ASC")
    fun getHotPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotProducts(hotProducts: List<HotProductEntity>)

    @Query("DELETE FROM hot_products")
    suspend fun deleteAllHotProducts()
}