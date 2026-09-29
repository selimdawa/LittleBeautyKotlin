package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.db.FavoriteDao
import com.flatcode.beautytouch.db.InterestedDao
import com.flatcode.beautytouch.db.PostDao
import com.flatcode.beautytouch.db.ShoppingCenterDao
import com.flatcode.beautytouch.db.SliderDao
import com.flatcode.beautytouch.model.FavoriteEntity
import com.flatcode.beautytouch.model.InterestedEntity
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.model.SliderEntity
import com.flatcode.beautytouch.model.Tools
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
import kotlinx.coroutines.flow.map
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
    private val interestedDao: InterestedDao,
    private val sliderDao: SliderDao
) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun getFavoritePosts(userId: String): Flow<List<Post>> {
        syncFavorites(userId)
        return favoriteDao.getFavoritePosts(userId)
    }

    fun getFavoritePosts(publisher: String, appName: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        val uid = auth.currentUser?.uid
        if (uid.isNullOrEmpty()) {
            trySend(Resource.Success(emptyList()))
            return@callbackFlow
        }
        val listener = database.getReference(DATA.SAVES).child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val saveIds = snapshot.children.mapNotNull { it.key }.toSet()
                database.getReference(DATA.POSTS).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(postsSnapshot: DataSnapshot) {
                        val list = mutableListOf<Post>()
                        val favList = mutableListOf<FavoriteEntity>()
                        for (child in postsSnapshot.children) {
                            val post = child.getValue(Post::class.java)
                            if (post != null && post.postid in saveIds && post.publisher == publisher && post.appName == appName) {
                                post.isSaved = true
                                list.add(post)
                                favList.add(FavoriteEntity(uid, post.postid))
                            }
                        }
                        repositoryScope.launch {
                            postDao.insertPosts(list)
                            favoriteDao.insertFavorites(favList)
                        }
                        trySend(Resource.Success(list))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(error.message))
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })
        awaitClose { database.getReference(DATA.SAVES).child(uid).removeEventListener(listener) }
    }

    fun getFavoriteShoppingCenters(userId: String): Flow<List<ShoppingCenter>> {
        syncFavorites(userId)
        return favoriteDao.getFavoriteShoppingCenters(userId)
    }

    fun getFavoriteCount(userId: String): Flow<Int> {
        syncFavorites(userId)
        return favoriteDao.getFavoriteCount(userId)
    }

    fun getInterestedTools(userId: String): Flow<List<Tools>> {
        syncInterested(userId, DATA.M_TOOLS)
        return interestedDao.getInterestedTools(userId, DATA.M_TOOLS)
    }

    fun getInterestedShoppingCenters(userId: String): Flow<List<ShoppingCenter>> {
        syncInterested(userId, DATA.SHOPPING_CENTERS)
        return interestedDao.getInterestedShoppingCenters(userId, DATA.SHOPPING_CENTERS)
    }

    fun getInterestedCount(userId: String, databaseName: String): Flow<Int> {
        syncInterested(userId, databaseName)
        return interestedDao.getInterestedCount(userId, databaseName)
    }

    fun getSliderImages(): Flow<List<String>> {
        syncSliderImages()
        return sliderDao.getSliderImages().map { list ->
            list.map { it.image }
        }
    }

    fun getImageSliderUrls(): Flow<Resource<List<String>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = database.getReference(DATA.IMAGE_LINKS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (child in snapshot.children) {
                    val url = child.child(DATA.IMAGE_URL).value?.toString() ?: (child.value as? String)
                    if (!url.isNullOrEmpty() && url != "null") {
                        list.add(url)
                    }
                }
                trySend(Resource.Success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })
        awaitClose { database.getReference(DATA.IMAGE_LINKS).removeEventListener(listener) }
    }

    fun getHotProducts(publisher: String, appName: String): Flow<Resource<List<Post>>> =
        getAllPosts(publisher, appName)

    fun getPostDetails(postId: String): Flow<Resource<Post>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = database.getReference(DATA.POSTS).child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val post = snapshot.getValue(Post::class.java)
                    if (post != null) {
                        trySend(Resource.Success(post))
                    } else {
                        trySend(Resource.Error("Post not found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message))
                }
            })
        awaitClose { database.getReference(DATA.POSTS).child(postId).removeEventListener(listener) }
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

    fun getPostsByCategory(category: String, publisher: String, appName: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        repositoryScope.launch {
            val localPosts = postDao.getPostsByCategory(category, publisher, appName).first()
            if (localPosts.isNotEmpty()) {
                trySend(Resource.Success(localPosts))
            }
        }
        val listener = database.getReference(DATA.POSTS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java)
                    if (post != null && post.category == category && post.publisher == publisher && post.appName == appName) {
                        list.add(post)
                    }
                }
                repositoryScope.launch {
                    postDao.insertPosts(list)
                }
                trySend(Resource.Success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })
        awaitClose { database.getReference(DATA.POSTS).removeEventListener(listener) }
    }

    fun getShoppingCenters(publisher: String, appName: String): Flow<Resource<List<ShoppingCenter>>> = callbackFlow {
        trySend(Resource.Loading)
        repositoryScope.launch {
            val localList = shoppingCenterDao.getShoppingCenters(publisher, appName).first()
            if (localList.isNotEmpty()) {
                trySend(Resource.Success(localList))
            }
        }
        val listener = database.getReference(DATA.SHOPPING_CENTERS).addValueEventListener(object : ValueEventListener {
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
                trySend(Resource.Success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        })
        awaitClose { database.getReference(DATA.SHOPPING_CENTERS).removeEventListener(listener) }
    }

    suspend fun toggleFavorite(itemId: String, userId: String, isFavorite: Boolean) {
        try {
            val ref = database.getReference(DATA.SAVES).child(userId).child(itemId)
            if (isFavorite) {
                ref.setValue(true).await()
                favoriteDao.insertFavorite(FavoriteEntity(userId, itemId))
            } else {
                ref.removeValue().await()
                favoriteDao.deleteFavorite(userId, itemId)
            }
        } catch (_: Exception) {
            if (isFavorite) {
                favoriteDao.insertFavorite(FavoriteEntity(userId, itemId))
            } else {
                favoriteDao.deleteFavorite(userId, itemId)
            }
        }
    }

    private fun syncShoppingCenters() {
        database.getReference(DATA.SHOPPING_CENTERS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ShoppingCenter>()
                    for (child in snapshot.children) {
                        child.getValue(ShoppingCenter::class.java)?.let { list.add(it) }
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

    private fun syncFavorites(userId: String) {
        if (userId.isEmpty()) return
        database.getReference(DATA.SAVES).child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favList =
                        snapshot.children.mapNotNull { it.key }.map { FavoriteEntity(userId, it) }
                    repositoryScope.launch {
                        favoriteDao.deleteAllFavoritesForUser(userId)
                        favoriteDao.insertFavorites(favList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncFavorites failed")
                }
            })
    }

    private fun syncInterested(userId: String, databaseName: String) {
        if (userId.isEmpty()) return
        database.getReference(DATA.SAVES).child(userId).child(databaseName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, databaseName, it) }
                    repositoryScope.launch {
                        interestedDao.deleteAllInterestedForUser(userId, databaseName)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncInterested failed")
                }
            })
    }

    private fun syncSliderImages() {
        database.getReference(DATA.IMAGE_LINKS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sliderList = mutableListOf<SliderEntity>()
                var index = 0
                for (child in snapshot.children) {
                    val id = child.key ?: index.toString()
                    val url = child.child(DATA.IMAGE_URL).value?.toString()
                        ?: (child.value as? String)
                    if (!url.isNullOrEmpty() && url != "null") {
                        sliderList.add(SliderEntity(id, url, index++))
                    }
                }
                repositoryScope.launch {
                    sliderDao.deleteAllSliderImages()
                    sliderDao.insertSliderImages(sliderList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncSliderImages failed")
            }
        })
    }

    fun getCategoryCount(
        category: String, publisher: String, appName: String
    ): Flow<Resource<Int>> = callbackFlow {
        trySend(Resource.Loading)
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                val posts = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java)
                    if (post != null) {
                        posts.add(post)
                        if (post.category == category && post.publisher == publisher && post.appName == appName) {
                            count++
                        }
                    }
                }
                repositoryScope.launch {
                    postDao.insertPosts(posts)
                }
                trySend(Resource.Success(count))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getAllPosts(publisher: String, appName: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localPosts = postDao.getPostsByPublisher(publisher, appName).first()
            if (localPosts.isNotEmpty()) {
                trySend(Resource.Success(localPosts))
            }
        }

        val postsRef = database.getReference(DATA.POSTS)
        val likesRef = database.getReference(DATA.LIKES)
        val savesRef = database.getReference(DATA.SAVES).child(auth.currentUser?.uid ?: "")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    val post = child.getValue(Post::class.java)
                    if (post != null) {
                        list.add(post)
                    }
                }

                likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(likesSnapshot: DataSnapshot) {
                        savesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(savesSnapshot: DataSnapshot) {
                                val enrichedList = list.map { post ->
                                    val postLikes = likesSnapshot.child(post.postid)
                                    post.apply {
                                        nrLikes = postLikes.childrenCount.toInt()
                                        isLiked =
                                            postLikes.child(auth.currentUser?.uid ?: "").exists()
                                        isSaved = savesSnapshot.child(post.postid).exists()
                                    }
                                }
                                val filtered = enrichedList.filter {
                                    it.publisher == publisher && it.appName == appName
                                }
                                repositoryScope.launch {
                                    postDao.insertPosts(filtered)
                                }
                                trySend(Resource.Success(filtered))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                trySend(Resource.Error(error.message))
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(error.message))
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        postsRef.addValueEventListener(listener)
        awaitClose { postsRef.removeEventListener(listener) }
    }
}