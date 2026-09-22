package com.flatcode.beautytouch.db

import androidx.room.*
import com.flatcode.beautytouch.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE postid = :postId")
    fun getPostById(postId: String): Flow<Post?>

    @Query("SELECT * FROM posts WHERE category = :category AND publisher = :publisher AND appName = :appName")
    fun getPostsByCategory(category: String, publisher: String, appName: String): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE publisher = :publisher AND appName = :appName")
    fun getPostsByPublisher(publisher: String, appName: String): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}