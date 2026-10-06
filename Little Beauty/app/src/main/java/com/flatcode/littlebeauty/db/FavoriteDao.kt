package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.FavoriteEntity
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.model.ShoppingCenter
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT posts.* FROM posts INNER JOIN favorites ON posts.postid = favorites.itemId WHERE favorites.userId = :userId ORDER BY posts.postid ASC")
    fun getFavoritePosts(userId: String): Flow<List<Post>>

    @Query("SELECT shopping_centers.* FROM shopping_centers INNER JOIN favorites ON shopping_centers.id = favorites.itemId WHERE favorites.userId = :userId ORDER BY shopping_centers.id ASC")
    fun getFavoriteShoppingCenters(userId: String): Flow<List<ShoppingCenter>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    fun getFavoriteCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemId = :itemId")
    suspend fun deleteFavorite(userId: String, itemId: String)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllFavoritesForUser(userId: String)
}