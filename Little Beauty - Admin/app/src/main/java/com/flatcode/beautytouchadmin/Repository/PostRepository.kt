package com.flatcode.beautytouchadmin.repository

import android.net.Uri
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.cloudinary.Cloudinary

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val cloudinary: Cloudinary
) {

    fun getPosts(type: String): Flow<List<Post>> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post?.aname == DATA.BEAUTY_TOUCH) {
                        when (type) {
                            DATA.ALL -> list.add(post)
                            DATA.SKIN -> if (post.category == DATA.SKIN_PRODUCTS) list.add(post)
                            DATA.HAIR -> if (post.category == DATA.HAIR_PRODUCTS) list.add(post)
                        }
                    }
                }
                trySend(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                close(databaseError.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPost(postId: String): Flow<Post?> = callbackFlow {
        val reference = database.getReference(DATA.POSTS).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Post::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadImage(postId: String, imageUri: Uri, extension: String): String {
        // TODO: Replace with Cloudinary implementation
        return ""
    }

    suspend fun addPost(postData: Map<String, Any?>) {
        val ref = database.getReference(DATA.POSTS)
        val id = postData["postid"] as String
        ref.child(id).setValue(postData).await()
    }

    suspend fun updatePost(postId: String, postData: Map<String, Any?>) {
        val reference = database.getReference(DATA.POSTS)
        reference.child(postId).updateChildren(postData).await()
    }

    fun generatePostId(): String? = database.getReference(DATA.POSTS).push().key

    fun getSavedPostIds(userId: String): Flow<List<String>> = callbackFlow {
        val reference = database.getReference(DATA.SAVES).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { list.add(it) }
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getAllPosts(): Flow<List<Post>> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (data in snapshot.children) {
                    val post = data.getValue(Post::class.java)
                    if (post != null) list.add(post)
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getLikesCount(postId: String): Flow<Long> = callbackFlow {
        val reference = database.getReference(DATA.LIKES).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun isLiked(postId: String, userId: String): Flow<Boolean> = callbackFlow {
        val reference = database.getReference(DATA.LIKES).child(postId).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun toggleLike(postId: String, userId: String, isLiked: Boolean) {
        val reference = database.getReference(DATA.LIKES).child(postId).child(userId)
        if (isLiked) {
            reference.removeValue().await()
        } else {
            reference.setValue(true).await()
        }
    }

    suspend fun toggleSave(postId: String, userId: String, isSaved: Boolean) {
        val reference = database.getReference(DATA.SAVES).child(userId).child(postId)
        if (isSaved) {
            reference.removeValue().await()
        } else {
            reference.setValue(true).await()
        }
    }

    suspend fun deletePost(postId: String) {
        database.getReference(DATA.POSTS).child(postId).removeValue().await()
    }
}
