package com.flatcode.beautytouchadmin.repository

import android.net.Uri
import com.cloudinary.Cloudinary
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val database: FirebaseDatabase, private val cloudinary: Cloudinary
) {

    fun getUsers(): Flow<List<User>> = callbackFlow {
        val reference = database.getReference(DATA.USERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null && user.id != DATA.FirebaseUserUid) {
                        list.add(user)
                    }
                }
                list.reverse()
                trySend(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                close(databaseError.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getUser(userId: String): Flow<User?> = callbackFlow {
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

    suspend fun uploadProfileImage(userId: String, imageUri: Uri, extension: String): String =
        withContext(Dispatchers.IO) {
            try {
                val options = mapOf(
                    "public_id" to userId, "folder" to "Users/Profiles"
                )
                val result = cloudinary.uploader().upload(imageUri.toString(), options)
                result["secure_url"] as String
            } catch (_: Exception) {
                ""
            }
        }

    suspend fun updateProfile(userId: String, data: Map<String, Any?>) {
        database.getReference(DATA.USERS).child(userId).updateChildren(data).await()
    }

    fun getUsersOrdered(orderBy: String, limit: Int = 0): Flow<List<User>> = callbackFlow {
        var query = database.getReference(DATA.USERS).orderByChild(orderBy)
        if (limit > 0) {
            query = query.limitToLast(limit)
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (data in snapshot.children) {
                    if (data.child(orderBy).exists()) {
                        val item = data.getValue(User::class.java)
                        if (item != null) list.add(item)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }
}
