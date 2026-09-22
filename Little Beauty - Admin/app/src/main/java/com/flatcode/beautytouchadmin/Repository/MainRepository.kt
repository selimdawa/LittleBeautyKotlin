package com.flatcode.beautytouchadmin.repository

import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MainRepository @Inject constructor(private val database: FirebaseDatabase) {

    fun getUsersCount(): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.USERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user?.id != null) count++
                }
                trySend(count - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getHotProductsCount(): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.HOT_PRODUCT)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPostsCount(userId: String): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val item = data.getValue(Post::class.java)
                    if (item?.publisher == userId) count++
                }
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getShoppingCentersCount(userId: String): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val item = data.getValue(Post::class.java)
                    if (item?.publisher == userId) count++
                }
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getSliderShowCount(): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.SLIDER_SHOW)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getUserInfo(userId: String): Flow<User?> = callbackFlow {
        val reference = database.getReference(DATA.USERS).child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(User::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}
