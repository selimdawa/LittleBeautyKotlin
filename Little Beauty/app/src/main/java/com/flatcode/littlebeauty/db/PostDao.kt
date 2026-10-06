package com.flatcode.littlebeauty.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlebeauty.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY postid ASC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE postid = :postId")
    fun getPostById(postId: String): Flow<Post?>

    @Query("SELECT * FROM posts WHERE category = :category ORDER BY postid ASC")
    fun getPostsByCategory(category: String): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE category = :category AND (publisher IS NULL OR publisher = '' OR publisher = :publisher) AND (appName IS NULL OR appName = '' OR appName = :appName) ORDER BY postid ASC")
    fun getPostsByCategory(category: String, publisher: String, appName: String): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE (publisher IS NULL OR publisher = '' OR publisher = :publisher) AND (appName IS NULL OR appName = '' OR appName = :appName) ORDER BY postid ASC")
    fun getPostsByPublisher(publisher: String, appName: String): Flow<List<Post>>

    @Query("UPDATE posts SET isLiked = :isLiked, nrLikes = :nrLikes WHERE postid = :postId")
    suspend fun updatePostLikeStatus(postId: String, isLiked: Boolean, nrLikes: Int)

    @Query("UPDATE posts SET isSaved = :isSaved WHERE postid = :postId")
    suspend fun updatePostSaveStatus(postId: String, isSaved: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}