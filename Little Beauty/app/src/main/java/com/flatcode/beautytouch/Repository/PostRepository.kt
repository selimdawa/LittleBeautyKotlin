package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.db.FavoriteDao
import com.flatcode.beautytouch.db.HotProductDao
import com.flatcode.beautytouch.db.PostDao
import com.flatcode.beautytouch.db.ShoppingCenterDao
import com.flatcode.beautytouch.model.FavoriteEntity
import com.flatcode.beautytouch.model.HotProductEntity
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
    private val favoriteDao: FavoriteDao,
    private val hotProductDao: HotProductDao
) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun getPostsByCategory(category: String, publisher: String, appName: String): Flow<List<Post>> {
        syncPosts(publisher, appName)
        return postDao.getPostsByCategory(category)
    }

    fun getAllPosts(publisher: String, appName: String): Flow<List<Post>> {
        syncPosts(publisher, appName)
        return postDao.getAllPosts()
    }

    fun getFavoritePosts(): Flow<List<Post>> {
        val uid = auth.currentUser?.uid ?: ""
        return favoriteDao.getFavoritePosts(uid)
    }

    fun getHotPosts(publisher: String, appName: String): Flow<List<Post>> {
        syncPosts(publisher, appName)
        syncHotProducts()
        return hotProductDao.getHotPosts()
    }

    fun getShoppingCenters(publisher: String, appName: String): Flow<List<ShoppingCenter>> {
        syncShoppingCenters(publisher, appName)
        return shoppingCenterDao.getAllShoppingCenters()
    }

    fun getPostDetails(postId: String): Flow<Post?> {
        syncPostById(postId)
        return postDao.getPostById(postId)
    }

    private fun syncPostById(postId: String) {
        val uid = auth.currentUser?.uid ?: ""
        val postRef = database.getReference(DATA.POSTS).child(postId)
        val likesRef = database.getReference(DATA.LIKES).child(postId)
        val savesRef = if (uid.isNotEmpty()) database.getReference(DATA.SAVES).child(uid)
            .child(postId) else null

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java) ?: return
                if (post.postid.isEmpty() && snapshot.key != null) {
                    post.postid = snapshot.key!!
                }

                likesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(likesSnapshot: DataSnapshot) {
                        val processPost: (Boolean) -> Unit = { isSaved ->
                            post.isLiked = uid.isNotEmpty() && likesSnapshot.child(uid).exists()
                            post.nrLikes = likesSnapshot.childrenCount.toInt()
                            post.isSaved = isSaved

                            repositoryScope.launch {
                                postDao.insertPost(post)
                            }
                        }

                        if (savesRef != null) {
                            savesRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(savesSnapshot: DataSnapshot) {
                                    processPost(savesSnapshot.exists())
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    processPost(false)
                                }
                            })
                        } else {
                            processPost(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncPostById failed")
            }
        })
    }

    fun syncPosts(publisher: String, appName: String) {
        val uid = auth.currentUser?.uid ?: ""
        val postsRef = database.getReference(DATA.POSTS)
        val likesRef = database.getReference(DATA.LIKES)
        val savesRef = if (uid.isNotEmpty()) database.getReference(DATA.SAVES).child(uid) else null

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(postsSnapshot: DataSnapshot) {
                likesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(likesSnapshot: DataSnapshot) {
                        val processWithSaves: (DataSnapshot?) -> Unit = { savesSnapshot ->
                            val list = mutableListOf<Post>()
                            val favoriteEntities = mutableListOf<FavoriteEntity>()

                            for (child in postsSnapshot.children) {
                                val post = child.getValue(Post::class.java)
                                if (post != null) {
                                    if (post.postid.isEmpty() && child.key != null) {
                                        post.postid = child.key!!
                                    }
                                    if ((post.publisher.isNullOrEmpty() || post.publisher == publisher) &&
                                        (post.appName.isNullOrEmpty() || post.appName == appName)
                                    ) {
                                        post.isLiked =
                                            uid.isNotEmpty() && likesSnapshot.child(post.postid)
                                                .child(uid).exists()
                                        post.nrLikes =
                                            likesSnapshot.child(post.postid).childrenCount.toInt()
                                        post.isSaved =
                                            uid.isNotEmpty() && (savesSnapshot?.child(post.postid)
                                                ?.exists() == true)

                                        if (post.isSaved && uid.isNotEmpty()) {
                                            favoriteEntities.add(FavoriteEntity(uid, post.postid))
                                        }
                                        list.add(post)
                                    }
                                }
                            }

                            repositoryScope.launch {
                                postDao.insertPosts(list)
                                if (uid.isNotEmpty()) {
                                    favoriteDao.insertFavorites(favoriteEntities)
                                }
                            }
                        }

                        if (savesRef != null) {
                            savesRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(savesSnapshot: DataSnapshot) {
                                    processWithSaves(savesSnapshot)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    processWithSaves(null)
                                }
                            })
                        } else {
                            processWithSaves(null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.e(error.toException(), "syncLikes failed")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncPosts failed")
            }
        })
    }

    fun syncHotProducts() {
        val hotRef = database.getReference(DATA.HOT_PRODUCT)
        hotRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<HotProductEntity>()
                for (child in snapshot.children) {
                    child.key?.let {
                        list.add(HotProductEntity(id = it))
                    }
                }
                repositoryScope.launch {
                    hotProductDao.deleteAllHotProducts()
                    hotProductDao.insertHotProducts(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncHotProducts failed")
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
                        if (center != null) {
                            if (center.id.isEmpty() && child.key != null) {
                                center.id = child.key!!
                            }
                            if ((center.publisher.isNullOrEmpty() || center.publisher == publisher) &&
                                (center.appName.isNullOrEmpty() || center.appName == appName)
                            ) {
                                list.add(center)
                            }
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