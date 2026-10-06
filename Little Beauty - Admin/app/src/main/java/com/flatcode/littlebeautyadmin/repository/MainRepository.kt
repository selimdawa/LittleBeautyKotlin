package com.flatcode.littlebeautyadmin.repository

import com.flatcode.littlebeautyadmin.model.User
import com.flatcode.littlebeautyadmin.utils.DATA
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
                    if (user != null) count++
                }
                trySend(if (count > 0) count - 1 else 0)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(0)
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
                trySend(0)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPostsCount(userId: String): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(0)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getShoppingCentersCount(userId: String): Flow<Int> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(0)
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
                trySend(0)
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
                trySend(null)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}