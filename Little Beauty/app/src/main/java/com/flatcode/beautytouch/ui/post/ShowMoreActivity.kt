package com.flatcode.beautytouch.ui.post

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouch.databinding.ActivityShowMoreBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ShowMoreActivity : BaseActivity() {

    private lateinit var binding: ActivityShowMoreBinding
    private val context: Context = this@ShowMoreActivity
    private val viewModel: PostViewModel by viewModels()

    private lateinit var adapter: ProductsStaggeredAdapter

    private var type: String? = null
    private var name: String? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private var selectedCategoryFilter: String = DATA.ALL
    private var fullPostsList: List<Post> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE)
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME)

        setupUI()
        setupAdapter()
        observeViewModel()
    }

    private fun setupUI() {
        binding.toolbar.nameSpace.text = name ?: ""
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.root.getChildAt(0).visibility = View.VISIBLE
                    binding.toolbar.root.getChildAt(1).visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                } else {
                    finish()
                }
            }
        })

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.root.getChildAt(0).visibility = View.GONE
            binding.toolbar.root.getChildAt(1).visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering posts")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.all.setOnClickListener {
            selectedCategoryFilter = DATA.ALL
            applyCategoryFilter()
        }

        binding.skin.setOnClickListener {
            selectedCategoryFilter = DATA.SKIN_PRODUCTS
            applyCategoryFilter()
        }

        binding.hair.setOnClickListener {
            selectedCategoryFilter = DATA.HAIR_PRODUCTS
            applyCategoryFilter()
        }
    }

    private fun applyCategoryFilter() {
        val filtered = when (selectedCategoryFilter) {
            DATA.SKIN_PRODUCTS -> fullPostsList.filter { it.category == DATA.SKIN_PRODUCTS }
            DATA.HAIR_PRODUCTS -> fullPostsList.filter { it.category == DATA.HAIR_PRODUCTS }
            else -> fullPostsList
        }

        adapter.setFullList(filtered)

        binding.progress.visibility = View.GONE
        if (filtered.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyText.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
        }
        binding.toolbar.number.text = MessageFormat.format("( {0} )", filtered.size)
    }

    private fun setupAdapter() {
        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postsByCategory.collect { posts ->
                    fullPostsList = posts
                    applyCategoryFilter()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (type == DATA.HOT_PRODUCT) {
            viewModel.loadHotProducts(publisher, appName)
        } else {
            viewModel.loadAllPosts(publisher, appName)
        }
    }
}