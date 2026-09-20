package com.flatcode.beautytouch.repository

import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.db.PostDao
import com.flatcode.beautytouch.db.ShoppingCenterDao
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
class PostRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val postDao: PostDao,
    private val shoppingCenterDao: ShoppingCenterDao
) {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun getCategoryCount(category: String, publisher: String, aname: String): Flow<Resource<Int>> = callbackFlow {
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
                        if (post.category == category && post.publisher == publisher && post.aname == aname) {
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

    fun getAllPosts(publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localPosts = postDao.getPostsByPublisher(publisher, aname).first()
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
                
                // Fetch likes and saves to enrich the list
                likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(likesSnapshot: DataSnapshot) {
                        savesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(savesSnapshot: DataSnapshot) {
                                val enrichedList = list.map { post ->
                                    val postLikes = likesSnapshot.child(post.postid)
                                    post.apply {
                                        nrLikes = postLikes.childrenCount.toInt()
                                        isLiked = postLikes.child(auth.currentUser?.uid ?: "").exists()
                                        isSaved = savesSnapshot.child(post.postid).exists()
                                    }
                                }
                                
                                repositoryScope.launch {
                                    postDao.insertPosts(enrichedList)
                                }

                                val filteredList = enrichedList.filter { it.publisher == publisher && it.aname == aname }
                                trySend(Resource.Success(filteredList))
                            }
                            override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                        })
                    }
                    override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        postsRef.addValueEventListener(listener)
        awaitClose { postsRef.removeEventListener(listener) }
    }

    fun getHotProducts(publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localPosts = postDao.getPostsByPublisher(publisher, aname).first()
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
                                        isLiked = postLikes.child(auth.currentUser?.uid ?: "").exists()
                                        isSaved = savesSnapshot.child(post.postid).exists()
                                    }
                                }
                                
                                repositoryScope.launch {
                                    postDao.insertPosts(enrichedList)
                                }

                                val filteredList = enrichedList.filter { it.publisher == publisher && it.aname == aname }
                                trySend(Resource.Success(filteredList))
                            }
                            override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                        })
                    }
                    override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        postsRef.addValueEventListener(listener)
        awaitClose { postsRef.removeEventListener(listener) }
    }

    fun getFavoritePosts(publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }
        val savesRef = database.getReference(DATA.SAVES).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(savesSnapshot: DataSnapshot) {
                val saveIds = savesSnapshot.children.mapNotNull { it.key }
                val postsRef = database.getReference(DATA.POSTS)
                postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(postsSnapshot: DataSnapshot) {
                        val posts = mutableListOf<Post>()
                        for (child in postsSnapshot.children) {
                            child.getValue(Post::class.java)?.let { posts.add(it) }
                        }
                        
                        enrichPosts(posts) { enrichedPosts ->
                            repositoryScope.launch {
                                postDao.insertPosts(enrichedPosts)
                            }
                            val filteredPosts = enrichedPosts.filter { 
                                it.publisher == publisher && it.aname == aname && it.postid in saveIds 
                            }
                            trySend(Resource.Success(filteredPosts))
                        }
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
        savesRef.addValueEventListener(listener)
        awaitClose { savesRef.removeEventListener(listener) }
    }

    fun getTopVotedPosts(publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        val reference = database.getReference(DATA.POSTS).orderByChild(DATA.VIEWS_COUNT)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    child.getValue(Post::class.java)?.let { list.add(it) }
                }
                
                enrichPosts(list) { enrichedList ->
                    repositoryScope.launch {
                        postDao.insertPosts(enrichedList)
                    }
                    val filteredList = enrichedList.filter { it.publisher == publisher && it.aname == aname }
                    trySend(Resource.Success(filteredList.reversed()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPostsByCategory(category: String, publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localPosts = postDao.getPostsByCategory(category, publisher, aname).first()
            if (localPosts.isNotEmpty()) {
                trySend(Resource.Success(localPosts))
            }
        }

        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    child.getValue(Post::class.java)?.let { list.add(it) }
                }
                
                enrichPosts(list) { enrichedList ->
                    repositoryScope.launch {
                        postDao.insertPosts(enrichedList)
                    }
                    val filteredList = enrichedList.filter { it.category == category && it.publisher == publisher && it.aname == aname }
                    trySend(Resource.Success(filteredList))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPostsBySearch(query: String, publisher: String, aname: String): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        val reference = database.getReference(DATA.POSTS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Post>()
                for (child in snapshot.children) {
                    child.getValue(Post::class.java)?.let { list.add(it) }
                }
                
                enrichPosts(list) { enrichedList ->
                    repositoryScope.launch {
                        postDao.insertPosts(enrichedList)
                    }
                    val filteredList = enrichedList.filter { 
                        it.publisher == publisher && it.aname == aname &&
                        (it.name?.contains(query, ignoreCase = true) == true)
                    }
                    trySend(Resource.Success(filteredList))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getPostDetails(postId: String): Flow<Resource<Post>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localPost = postDao.getPostById(postId).first()
            if (localPost != null) {
                trySend(Resource.Success(localPost))
            }
        }

        val postRef = database.getReference(DATA.POSTS).child(postId)
        val likesRef = database.getReference(DATA.LIKES).child(postId)
        val savesRef = database.getReference(DATA.SAVES).child(auth.currentUser?.uid ?: "").child(postId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(likesSnapshot: DataSnapshot) {
                            savesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(savesSnapshot: DataSnapshot) {
                                    post.apply {
                                        nrLikes = likesSnapshot.childrenCount.toInt()
                                        isLiked = likesSnapshot.child(auth.currentUser?.uid ?: "").exists()
                                        isSaved = savesSnapshot.exists()
                                    }
                                    repositoryScope.launch {
                                        postDao.insertPost(post)
                                    }
                                    trySend(Resource.Success(post))
                                }
                                override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                            })
                        }
                        override fun onCancelled(error: DatabaseError) { trySend(Resource.Error(error.message)) }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        postRef.addValueEventListener(listener)
        awaitClose { postRef.removeEventListener(listener) }
    }

    fun getShoppingCenters(publisher: String, aname: String): Flow<Resource<List<ShoppingCenter>>> = callbackFlow {
        trySend(Resource.Loading)

        repositoryScope.launch {
            val localCenters = shoppingCenterDao.getShoppingCenters(publisher, aname).first()
            if (localCenters.isNotEmpty()) {
                trySend(Resource.Success(localCenters))
            }
        }

        val reference = database.getReference(DATA.SHOPPING_CENTERS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ShoppingCenter>()
                for (child in snapshot.children) {
                    val center = child.getValue(ShoppingCenter::class.java)
                    if (center != null) {
                        list.add(center)
                    }
                }
                repositoryScope.launch {
                    shoppingCenterDao.insertShoppingCenters(list)
                }

                val filteredList = list.filter { it.publisher == publisher && it.aname == aname }
                trySend(Resource.Success(filteredList))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun getImageSliderUrls(): Flow<Resource<List<String>>> = callbackFlow {
        trySend(Resource.Loading)
        val reference = database.getReference(DATA.IMAGE_LINKS)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val urls = snapshot.children.mapNotNull { it.value?.toString() }
                trySend(Resource.Success(urls))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    fun addPostView(postId: String) {
        val ref = database.getReference(DATA.POSTS).child(postId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentViews = snapshot.child(DATA.VIEWS_COUNT).value?.toString()?.toLong() ?: 0L
                val hashMap = HashMap<String, Any>()
                hashMap[DATA.VIEWS_COUNT] = currentViews + 1
                ref.updateChildren(hashMap)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun enrichPosts(posts: List<Post>, onComplete: (List<Post>) -> Unit) {
        val likesRef = database.getReference(DATA.LIKES)
        val savesRef = database.getReference(DATA.SAVES).child(auth.currentUser?.uid ?: "")

        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(likesSnapshot: DataSnapshot) {
                savesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(savesSnapshot: DataSnapshot) {
                        val enrichedList = posts.map { post ->
                            val postLikes = likesSnapshot.child(post.postid)
                            post.apply {
                                nrLikes = postLikes.childrenCount.toInt()
                                isLiked = postLikes.child(auth.currentUser?.uid ?: "").exists()
                                isSaved = savesSnapshot.child(post.postid).exists()
                            }
                        }
                        onComplete(enrichedList)
                    }
                    override fun onCancelled(error: DatabaseError) { onComplete(posts) }
                })
            }
            override fun onCancelled(error: DatabaseError) { onComplete(posts) }
        })
    }

    fun toggleLike(postId: String, isLiked: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val ref = database.reference.child(DATA.LIKES).child(postId).child(uid)
        if (!isLiked) ref.setValue(true) else ref.removeValue()
    }

    fun toggleSave(postId: String, isSaved: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val ref = database.reference.child(DATA.SAVES).child(uid).child(postId)
        if (!isSaved) ref.setValue(true) else ref.removeValue()
    }
}