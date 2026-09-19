package com.flatcode.beautytouchadmin.ui.shopping

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
    private val list = mutableListOf<ShoppingCenter?>()
    private var adapter: ShoppingCentersAdapter? = null
    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCentersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.shopping_centers)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        adapter = ShoppingCentersAdapter(context, list, object : ShoppingCentersAdapter.OnItemClickListener {
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
                    context.intentExtra(CLASS.SHOPPING_CENTRES_EDIT, DATA.SHOPPING_CENTER_ID, item.id)
                } else if (which == 1) {
                    showDeleteDialog(item)
                }
            }.show()
    }

    private fun showDeleteDialog(item: ShoppingCenter) {
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
        title.setText(R.string.do_you_want_to_delete_the_pharmacy)

        dialog.findViewById<View>(R.id.yes).setOnClickListener {
            viewModel.deleteCenter(item.id!!)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.centers.collect { centers ->
                    list.clear()
                    list.addAll(centers)
                    adapter?.list = list
                    adapter?.notifyDataSetChanged()

                    binding!!.bar.visibility = View.GONE
                    if (list.isNotEmpty()) {
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
