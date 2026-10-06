package com.flatcode.beautytouch.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.beautytouch.model.HotProductEntity
import com.flatcode.beautytouch.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface HotProductDao {

    @Query("SELECT posts.* FROM posts INNER JOIN hot_products ON posts.postid = hot_products.id")
    fun getHotPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotProducts(hotProducts: List<HotProductEntity>)

    @Query("DELETE FROM hot_products")
    suspend fun deleteAllHotProducts()
}