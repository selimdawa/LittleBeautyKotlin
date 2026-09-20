package com.flatcode.beautytouchadmin.repository

import android.net.Uri
import com.flatcode.beautytouchadmin.model.Tools
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

class ToolsRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val cloudinary: Cloudinary
) {

    fun getTools(): Flow<Tools?> = callbackFlow {
        val reference = database.getReference(DATA.M_TOOLS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Tools::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadImage(path: String, imageUri: Uri, extension: String): String {
        // TODO: Replace with Cloudinary implementation
        return ""
    }

    suspend fun updateTools(data: Map<String, Any?>) {
        database.getReference(DATA.M_TOOLS).updateChildren(data).await()
    }
}
