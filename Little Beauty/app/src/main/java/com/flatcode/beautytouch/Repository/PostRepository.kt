package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.db.FavoriteDao
import com.flatcode.beautytouch.db.PostDao
import com.flatcode.beautytouch.db.ShoppingCenterDao
import com.flatcode.beautytouch.model.FavoriteEntity
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.utils.DATA
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
class PostRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val postDao: PostDao,
    private val shoppingCenterDao: ShoppingCenterDao,
    private val favoriteDao: FavoriteDao
) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun getPostsByCategory(category: String, publisher: String, appName: String): Flow<List<Post>> {
        syncPosts(publisher, appName)
        return postDao.getPostsByCategory(category, publisher, appName)
    }

    fun getAllPosts(publisher: String, appName: String): Flow<List<Post>> {
        syncPosts(publisher, appName)
        return postDao.getPostsByPublisher(publisher, appName)
    }

    fun getShoppingCenters(publisher: String, appName: String): Flow<List<ShoppingCenter>> {
        syncShoppingCenters(publisher, appName)
        return shoppingCenterDao.getShoppingCenters(publisher, appName)
    }

    fun getPostDetails(postId: String): Flow<Post?> {
        syncPostById(postId)
        return postDao.getPostById(postId)
    }

    private fun syncPostById(postId: String) {
        database.getReference(DATA.POSTS).child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Post::class.java)?.let { post ->
                        repositoryScope.launch {
                            postDao.insertPost(post)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncPostById failed")
                }
            })
    }

    fun syncPosts(publisher: String, appName: String) {
        database.getReference(DATA.POSTS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java)
                    if (post != null && post.publisher == publisher && post.appName == appName) {
                        list.add(post)
                    }
                }
                repositoryScope.launch {
                    postDao.insertPosts(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncPosts failed")
            }
        })
    }

    fun syncShoppingCenters(publisher: String, appName: String) {
        database.getReference(DATA.SHOPPING_CENTERS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ShoppingCenter>()
                    for (child in snapshot.children) {
                        val center = child.getValue(ShoppingCenter::class.java)
                        if (center != null && center.publisher == publisher && center.appName == appName) {
                            list.add(center)
                        }
                    }
                    repositoryScope.launch {
                        shoppingCenterDao.insertShoppingCenters(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncShoppingCenters failed")
                }
            })
    }

    fun getImageSliderUrls(): Flow<List<String>> = callbackFlow {
        val listener = database.getReference(DATA.IMAGE_LINKS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    for (child in snapshot.children) {
                        val url = child.child(DATA.IMAGE_URL).value?.toString()
                            ?: (child.value as? String)
                        if (!url.isNullOrEmpty() && url != "null") {
                            list.add(url)
                        }
                    }
                    trySend(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(emptyList())
                }
            })
        awaitClose { database.getReference(DATA.IMAGE_LINKS).removeEventListener(listener) }
    }

    fun addPostView(postId: String) {
        repositoryScope.launch {
            try {
                val ref = database.getReference(DATA.POSTS).child(postId).child(DATA.VIEWS_COUNT)
                val snapshot = ref.get().await()
                val currentViews = snapshot.getValue(Long::class.java) ?: 0L
                ref.setValue(currentViews + 1).await()
            } catch (e: Exception) {
                Timber.e(e, "Error adding post view")
            }
        }
    }

    fun toggleLike(postId: String, isLiked: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        repositoryScope.launch {
            try {
                val ref = database.getReference(DATA.LIKES).child(postId).child(uid)
                if (isLiked) {
                    ref.removeValue().await()
                } else {
                    ref.setValue(true).await()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling like")
            }
        }
    }

    fun toggleSave(postId: String, isSaved: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        repositoryScope.launch {
            try {
                val ref = database.getReference(DATA.SAVES).child(uid).child(postId)
                if (isSaved) {
                    ref.removeValue().await()
                    favoriteDao.deleteFavorite(uid, postId)
                } else {
                    ref.setValue(true).await()
                    favoriteDao.insertFavorite(FavoriteEntity(uid, postId))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling save")
            }
        }
    }
}