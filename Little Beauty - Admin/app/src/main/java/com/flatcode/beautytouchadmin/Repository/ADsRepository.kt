package com.flatcode.beautytouchadmin.repository

import com.flatcode.beautytouchadmin.model.ADs
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

class ADsRepository @Inject constructor(private val database: FirebaseDatabase) {

    fun getAds(userId: String, orderBy: String): Flow<List<ADs>> = callbackFlow {
        val reference = database.getReference(DATA.M_AD).child(userId).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ADs>()
                for (data in snapshot.children) {
                    val item = data.getValue(ADs::class.java)
                    if (item != null) list.add(item)
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

    fun getUsersWithAds(orderBy: String): Flow<List<User>> = callbackFlow {
        val reference = database.getReference(DATA.USERS).orderByChild(orderBy)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (data in snapshot.children) {
                    val item = data.getValue(User::class.java)
                    if (item != null && (item.adLoad != 0 || item.adClick != 0)) {
                        list.add(item)
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
}