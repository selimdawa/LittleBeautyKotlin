package com.flatcode.beautytouchadmin.ui.shopping

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.model.ShoppingCenter
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.databinding.ActivityShoppingCentersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCentersActivity : AppCompatActivity() {

    private var binding: ActivityShoppingCentersBinding? = null
    private val context: Context = this@ShoppingCentersActivity
    private var adapter: ShoppingCentersAdapter? = null
    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCentersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.shopping_centers)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = ShoppingCentersAdapter(context, object : ShoppingCentersAdapter.OnItemClickListener {
            override fun onMoreClick(item: ShoppingCenter) {
                showMoreOptions(item)
            }
        })
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun showMoreOptions(item: ShoppingCenter) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(context)
            .setTitle("Choose...")
            .setItems(options) { _: DialogInterface?, which: Int ->
                if (which == 0) {
                    context.openActivity<ShoppingCentresEditActivity>(DATA.SHOPPING_CENTER_ID to item.id)
                } else if (which == 1) {
                    Dialog.showDeleteDialog(context, R.string.do_you_want_to_delete_the_pharmacy) {
                        viewModel.deleteCenter(item.id!!)
                    }
                }
            }.show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.centers.collect { centers ->
                    adapter?.submitList(centers)

                    binding!!.bar.visibility = View.GONE
                    if (centers.isNotEmpty()) {
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
        viewModel.fetchCenters()
    }
}
