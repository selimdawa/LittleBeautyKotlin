@file:Suppress("SpellCheckingInspection")

package com.flatcode.littlebeautyadmin.repository

import android.net.Uri
import com.flatcode.littlebeautyadmin.model.Post
import com.flatcode.littlebeautyadmin.utils.CloudinaryHelper
import com.flatcode.littlebeautyadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getPosts(type: String): Flow<List<Post>> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post != null) {
                        if (snapshot.key != null) {
                            post.postid = snapshot.key!!
                        }
                        when (type) {
                            DATA.ALL -> list.add(post)
                            DATA.SKIN -> if (post.category == DATA.SKIN_PRODUCTS) list.add(post)
                            DATA.HAIR -> if (post.category == DATA.HAIR_PRODUCTS) list.add(post)
                            else -> list.add(post)
                        }
                    }
                }
                trySend(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(emptyList())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPost(postId: String): Flow<Post?> = callbackFlow {
        val reference = database.getReference(DATA.POSTS).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                post?.let {
                    if (snapshot.key != null) {
                        it.postid = snapshot.key!!
                    }
                }
                trySend(post)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadImage(postId: String, imageUri: Uri): String = try {
        CloudinaryHelper.uploadFile(imageUri)
    } catch (_: Exception) {
        ""
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

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun isSaved(postId: String, userId: String): Flow<Boolean> = callbackFlow {
        val reference = database.getReference(DATA.SAVES).child(userId).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
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
                    if (post != null) {
                        if (data.key != null) {
                            post.postid = data.key!!
                        }
                        list.add(post)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
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

            override fun onCancelled(error: DatabaseError) {
                trySend(0L)
            }
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

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
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