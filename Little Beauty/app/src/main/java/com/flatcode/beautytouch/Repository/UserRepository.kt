package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.db.ToolsDao
import com.flatcode.beautytouch.db.UserDao
import com.flatcode.beautytouch.model.Reward
import com.flatcode.beautytouch.model.Tools
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    fun getUserInfo(): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading)
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        repositoryScope.launch {
            userDao.getUserById(uid).first()?.let {
                trySend(Resource.Success(it))
            }
        }

        val reference = database.getReference(DATA.USERS).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    repositoryScope.launch {
                        userDao.insertUser(user)
                    }
                    trySend(Resource.Success(user))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getAppTools(): Flow<Resource<Tools>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            toolsDao.getTools().first()?.let {
                trySend(Resource.Success(it))
            }
        }

        val reference = database.getReference(DATA.M_TOOLS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tools = snapshot.getValue(Tools::class.java)
                if (tools != null) {
                    repositoryScope.launch {
                        toolsDao.insertTools(tools)
                    }
                    trySend(Resource.Success(tools))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun logout() {
        auth.signOut()
        repositoryScope.launch {
            userDao.deleteAllUsers()
            toolsDao.deleteAllTools()
        }
    }

    fun updateProfile(username: String, imageUrl: String?): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val hashMap = HashMap<String, Any>()
        hashMap[DATA.USER_NAME] = username
        if (imageUrl != null) {
            hashMap[DATA.IMAGE_URL] = imageUrl
        }
        database.getReference(DATA.USERS).child(uid).updateChildren(hashMap).addOnSuccessListener {
            trySend(Resource.Success(true))
            // Local update will be triggered by ValueEventListener in getUserInfo
        }.addOnFailureListener { trySend(Resource.Error(it.message ?: "Update failed")) }
        awaitClose()
    }

    fun getPoints(year: String, session: String): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading)
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val reference = database.getReference(DATA.USERS).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val key = "${year}_$session"
                val value = snapshot.child(key).value?.toString() ?: "0"
                trySend(Resource.Success(value))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
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

    fun getLeaderboard(orderBy: String, limit: Int): Flow<Resource<List<User>>> = callbackFlow {
        trySend(Resource.Loading)
        val query = database.getReference(DATA.USERS).orderByChild(orderBy).limitToLast(limit)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
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
                trySend(Resource.Success(list.reversed())) // Top users first
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })
        awaitClose()
    }

    fun getRewards(): Flow<Resource<Reward>> = callbackFlow {
        trySend(Resource.Loading)
        val reference = database.getReference(DATA.M_REWARD)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reward = snapshot.getValue(Reward::class.java)
                if (reward != null) {
                    trySend(Resource.Success(reward))
                } else {
                    trySend(Resource.Error("Reward data not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }
}