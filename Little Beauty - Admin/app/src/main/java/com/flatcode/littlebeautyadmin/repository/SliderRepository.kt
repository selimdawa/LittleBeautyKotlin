package com.flatcode.littlebeautyadmin.repository

import android.net.Uri
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

class SliderRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    fun getSliders(): Flow<Map<String, String>> = callbackFlow {
        val reference = database.getReference(DATA.SLIDER_SHOW)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, String>()
                for (data in snapshot.children) {
                    if (data.key != null && data.value != null) {
                        map[data.key!!] = data.value.toString()
                    }
                }
                trySend(map)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyMap())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    suspend fun uploadSliderImage(name: String, imageUri: Uri): String = try {
        CloudinaryHelper.uploadFile(imageUri)
    } catch (_: Exception) {
        ""
    }

    suspend fun updateSlider(data: Map<String, Any?>) {
        database.getReference(DATA.SLIDER_SHOW).updateChildren(data).await()
    }
}
