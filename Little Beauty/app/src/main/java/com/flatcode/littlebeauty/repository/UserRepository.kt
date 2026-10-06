package com.flatcode.littlebeauty.repository

import com.flatcode.littlebeauty.db.ToolsDao
import com.flatcode.littlebeauty.db.UserDao
import com.flatcode.littlebeauty.model.Reward
import com.flatcode.littlebeauty.model.Tools
import com.flatcode.littlebeauty.model.User
import com.flatcode.littlebeauty.utils.DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val toolsDao: ToolsDao
) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun getUserInfo(): Flow<User?> {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            syncUser(uid)
            return userDao.getUserById(uid)
        }
        return callbackFlow {
            trySend(null)
            awaitClose()
        }
    }

    private fun syncUser(uid: String) {
        database.getReference(DATA.USERS).child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(User::class.java)?.let { user ->
                        repositoryScope.launch {
                            userDao.insertUser(user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncUser failed")
                }
            })
    }

    fun getAppTools(): Flow<Tools?> {
        syncTools()
        return toolsDao.getTools()
    }

    private fun syncTools() {
        database.getReference(DATA.M_TOOLS).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Tools::class.java)?.let { tools ->
                        repositoryScope.launch {
                            toolsDao.insertTools(tools)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncTools failed")
                }
            })
    }

    fun logout() {
        auth.signOut()
        repositoryScope.launch {
            userDao.deleteAllUsers()
            toolsDao.deleteAllTools()
        }
    }

    suspend fun updateProfile(username: String, imageUrl: String?): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.USER_NAME] = username
            if (imageUrl != null) {
                hashMap[DATA.IMAGE_URL] = imageUrl
            }
            database.getReference(DATA.USERS).child(uid).updateChildren(hashMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPoints(year: String, session: String): Flow<String> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            trySend("0")
            return@callbackFlow
        }
        val reference = database.getReference(DATA.USERS).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val key = "${year}_$session"
                val value = snapshot.child(key).value?.toString() ?: "0"
                trySend(value)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend("0")
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun addRewardPoint(year: String, session: String) {
        val uid = auth.currentUser?.uid ?: return
        val key = "${year}_$session"
        val ref = database.getReference(DATA.USERS).child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPoints = snapshot.child(key).value?.toString()?.toLong() ?: 0L
                ref.child(key).setValue(currentPoints + 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getLeaderboard(orderBy: String, limit: Int): Flow<List<User>> = callbackFlow {
        val query = database.getReference(DATA.USERS).orderByChild(orderBy).limitToLast(limit)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (child in snapshot.children) {
                    if (child.child(orderBy).exists()) {
                        child.getValue(User::class.java)?.let { user ->
                            user.points = child.child(orderBy).value?.toString()?.toInt() ?: 0
                            list.add(user)
                        }
                    }
                }
                repositoryScope.launch {
                    userDao.insertUsers(list)
                }
                trySend(list.reversed())
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getRewards(): Flow<Reward?> = callbackFlow {
        val reference = database.getReference(DATA.M_REWARD)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reward = snapshot.getValue(Reward::class.java)
                trySend(reward)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}