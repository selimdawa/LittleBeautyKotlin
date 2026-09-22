package com.flatcode.beautytouchadmin.repository

import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HotProductRepository @Inject constructor(private val database: FirebaseDatabase) {

    fun getHotProductIds(): Flow<List<String>> = callbackFlow {
        val reference = database.getReference(DATA.HOT_PRODUCT)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { list.add(it) }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPosts(): Flow<List<Post>> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (data in snapshot.children) {
                    val post = data.getValue(Post::class.java)
                    if (post?.publisher == DATA.PUBLISHER && post.aname == DATA.APP_NAME) {
                        list.add(post)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun addToHotProducts(postId: String) {
        database.getReference(DATA.HOT_PRODUCT).child(postId).setValue(true).await()
    }

    suspend fun removeFromHotProducts(postId: String) {
        database.getReference(DATA.HOT_PRODUCT).child(postId).removeValue().await()
    }
}