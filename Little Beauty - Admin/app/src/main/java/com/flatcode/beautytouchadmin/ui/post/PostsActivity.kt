package com.flatcode.beautytouchadmin.ui.post

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.flatcode.beautytouchadmin.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.databinding.ActivityPostsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsActivity : BaseActivity() {

    private var binding: ActivityPostsBinding? = null
    private val context: Context = this@PostsActivity
    private var adapter: MyPostsAdapter? = null
    private var type = DATA.ALL
    private val viewModel: PostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.my_posts)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = MyPostsAdapter(context, object : MyPostsAdapter.OnItemClickListener {
            override fun onMoreClick(post: Post) {
                showMoreOptions(post)
            }

            override fun onLikeClick(post: Post, isLiked: Boolean) {
                viewModel.toggleLike(post.postid, isLiked)
            }
        })
        binding!!.recyclerView.adapter = adapter

        binding!!.all.setOnClickListener {
            type = DATA.ALL
            viewModel.fetchPosts(type)
        }
        binding!!.hair.setOnClickListener {
            type = DATA.HAIR
            viewModel.fetchPosts(type)
        }
        binding!!.skin.setOnClickListener {
            type = DATA.SKIN
            viewModel.fetchPosts(type)
        }

        observeViewModel()
    }

    private fun showMoreOptions(post: Post) {
        val options = arrayOf(getString(R.string.edit), getString(R.string.delete))
        AlertDialog.Builder(context)
            .setTitle(R.string.choose)
            .setItems(options) { _: DialogInterface?, which: Int ->
                if (which == 0) {
                    context.openActivity<PostEditActivity>(DATA.POST_ID to post.postid)
                } else if (which == 1) {
                    showDeleteDialog(post)
                }
            }.show()
    }

    private fun showDeleteDialog(post: Post) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val title = dialog.findViewById<TextView>(R.id.title)
        title.setText(R.string.do_you_want_to_delete_the_post)

        dialog.findViewById<View>(R.id.yes).setOnClickListener {
            viewModel.deletePost(post.postid)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collect { posts ->
                    adapter?.submitList(posts)

                    binding!!.progress.visibility = View.GONE
                    if (posts.isNotEmpty()) {
                        binding!!.recyclerView.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                    } else {
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPosts(type)
    }
}
