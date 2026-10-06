package com.flatcode.littlebeauty.repository

import com.flatcode.littlebeauty.utils.DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth, private val database: FirebaseDatabase
) {

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val id = result.user?.uid ?: return Result.failure(Exception("User ID is null"))

            val hashMap = HashMap<String, Any?>()
            hashMap[DATA.ID] = id
            hashMap["started"] = "" + System.currentTimeMillis()
            hashMap["password"] = password
            hashMap["mversion"] = DATA.CURRENT_VERSION
            hashMap["imageurl"] = DATA.BASIC
            hashMap["username"] = name

            database.getReference(DATA.USERS).child(id).setValue(hashMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}