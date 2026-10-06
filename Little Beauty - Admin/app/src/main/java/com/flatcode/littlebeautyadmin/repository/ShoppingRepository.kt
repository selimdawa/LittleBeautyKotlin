package com.flatcode.littlebeautyadmin.repository

import android.net.Uri
import com.flatcode.littlebeautyadmin.model.ShoppingCenter
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

class ShoppingRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getShoppingCenters(): Flow<List<ShoppingCenter>> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<ShoppingCenter>()
                for (snapshot in dataSnapshot.children) {
                    val shoppingCenter = snapshot.getValue(ShoppingCenter::class.java)
                    if (shoppingCenter != null) {
                        if (shoppingCenter.id.isEmpty()) {
                            shoppingCenter.id = snapshot.key ?: ""
                        }
                        list.add(shoppingCenter)
                    }
                }
                list.reverse()
                trySend(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(emptyList())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getShoppingCenter(id: String): Flow<ShoppingCenter?> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS).child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(ShoppingCenter::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadImage(id: String, imageUri: Uri, index: String = ""): String = try {
        CloudinaryHelper.uploadFile(imageUri)
    } catch (_: Exception) {
        ""
    }

    suspend fun addShoppingCenter(data: Map<String, Any?>) {
        val ref = database.getReference(DATA.SHOPPING_CENTERS)
        val id = data["id"] as String
        ref.child(id).setValue(data).await()
    }

    suspend fun updateShoppingCenter(id: String, data: Map<String, Any?>) {
        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        reference.child(id).updateChildren(data).await()
    }

    fun generateId(): String? = database.getReference(DATA.SHOPPING_CENTERS).push().key

    suspend fun deleteShoppingCenter(id: String) {
        database.getReference(DATA.SHOPPING_CENTERS).child(id).removeValue().await()
    }
}