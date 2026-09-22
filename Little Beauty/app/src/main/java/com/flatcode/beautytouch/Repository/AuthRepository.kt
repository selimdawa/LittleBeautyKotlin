@file:Suppress("SpellCheckingInspection")

package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth, private val database: FirebaseDatabase
) {

    fun login(email: String, password: String): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                trySend(Resource.Success(true))
            }.addOnFailureListener {
                trySend(Resource.Error(it.message ?: "Login Failed"))
            }
        awaitClose()
    }

    fun register(
        name: String, email: String, password: String, number: String
    ): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result ->
                val id = result.user?.uid
                val hashMap = HashMap<String, Any?>()
                hashMap[DATA.ID] = id
                hashMap["started"] = "" + System.currentTimeMillis()
                hashMap["phonenumber"] = number
                hashMap["password"] = password
                hashMap["mversion"] = DATA.CURRENT_VERSION
                hashMap["imageurl"] = DATA.BASIC
                hashMap["username"] = name

                if (id != null) {
                    database.getReference(DATA.USERS).child(id).setValue(hashMap)
                        .addOnSuccessListener {
                            trySend(Resource.Success(true))
                        }.addOnFailureListener {
                            trySend(Resource.Error(it.message ?: "Failed to save user data"))
                        }
                }
            }.addOnFailureListener {
                trySend(Resource.Error(it.message ?: "Registration Failed"))
            }
        awaitClose()
    }

    fun forgetPassword(email: String): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success("Password reset has been sent to $email"))
                } else {
                    trySend(Resource.Error(task.exception?.message ?: "Failed to send reset email"))
                }
            }
        awaitClose()
    }
}