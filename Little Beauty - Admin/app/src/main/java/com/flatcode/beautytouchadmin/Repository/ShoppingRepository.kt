package com.flatcode.beautytouchadmin.repository

import android.net.Uri
import com.flatcode.beautytouchadmin.model.ShoppingCenter
import com.flatcode.beautytouchadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.cloudinary.Cloudinary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShoppingRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val cloudinary: Cloudinary
) {

    fun getShoppingCenters(): Flow<List<ShoppingCenter>> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<ShoppingCenter>()
                for (snapshot in dataSnapshot.children) {
                    val shoppingCenter = snapshot.getValue(ShoppingCenter::class.java)
                    if (shoppingCenter?.publisher == DATA.FirebaseUserUid && shoppingCenter.aname == DATA.APP_NAME) {
                        list.add(shoppingCenter)
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

    fun getShoppingCenter(id: String): Flow<ShoppingCenter?> = callbackFlow {
        val reference = database.getReference(DATA.SHOPPING_CENTERS).child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(ShoppingCenter::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadImage(id: String, imageUri: Uri, extension: String, index: String = ""): String =
        withContext(Dispatchers.IO) {
            try {
                val publicId = if (index.isEmpty()) id else "${id}_$index"
                val options = mapOf(
                    "public_id" to publicId,
                    "folder" to "ShoppingCenters"
                )
                val result = cloudinary.uploader().upload(imageUri.toString(), options)
                result["secure_url"] as String
            } catch (e: Exception) {
                ""
            }
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
