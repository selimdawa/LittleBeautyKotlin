package com.flatcode.beautytouchadmin.ui.post

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.flatcode.beautytouchadmin.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canhub.cropper.CropImageContract
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityPostAddBinding
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.checkStoragePermission
import com.flatcode.beautytouchadmin.utils.cropImageSquareOptions
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.setMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.flatcode.beautytouchadmin.utils.loadingDialog

@AndroidEntryPoint
class PostEditActivity : BaseActivity() {

    private var binding: ActivityPostAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var id: String? = null
    private var imageUri: Uri? = null
    private var dialog: Dialog? = null
    private var typePost: String? = DATA.EMPTY
    private val viewModel: PostActionViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImage()
        } else {
            Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        cropImage.launch(cropImageSquareOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding!!.image.setImageURI(imageUri)
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        id = intent.getStringExtra(DATA.POST_ID)

        dialog = loadingDialog(context)

        binding!!.toolbar.nameSpace.setText(R.string.edit_post)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding!!.typeOne.setOnClickListener {
            typePost = "Skin Products"
            binding!!.typeOne.setText(R.string.skin_products_selected)
            binding!!.typeTwo.setText(R.string.hair_products)
        }
        binding!!.typeTwo.setOnClickListener {
            typePost = "Hair Products"
            binding!!.typeOne.setText(R.string.skin_products)
            binding!!.typeTwo.setText(R.string.hair_products_selected)
        }

        binding!!.toolbar.nameSpace.setText(R.string.edit_post)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }
        binding!!.layoutImageProfile.setOnClickListener {
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        id?.let { viewModel.loadPost(it) }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { item ->
                    item?.let {
                        typePost = it.category
                        binding!!.image.loadImage(true, it.postimage)
                        binding!!.name.setText(it.name)
                        binding!!.price.setText(it.price)
                        binding!!.indications.setText(it.indications)
                        binding!!.howToUse.setText(it.use)
                        if (it.category == "Skin Products") {
                            binding!!.typeOne.setText(R.string.skin_products_selected)
                            binding!!.typeTwo.setText(R.string.hair_products)
                        } else if (it.category == "Hair Products") {
                            binding!!.typeOne.setText(R.string.skin_products)
                            binding!!.typeTwo.setText(R.string.hair_products_selected)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog!!.dismiss()
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val name = binding!!.name.text.toString().trim()
        val indications = binding!!.indications.text.toString().trim()
        val howToUse = binding!!.howToUse.text.toString().trim()
        val price = binding!!.price.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, R.string.enter_name, Toast.LENGTH_SHORT).show()
        } else if (indications.isEmpty()) {
            Toast.makeText(context, R.string.enter_indications, Toast.LENGTH_SHORT).show()
        } else if (howToUse.isEmpty()) {
            Toast.makeText(context, R.string.enter_how_to_use, Toast.LENGTH_SHORT).show()
        } else if (price.isEmpty()) {
            Toast.makeText(context, R.string.enter_price, Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage(getString(R.string.post_updating))
            dialog!!.show()
            viewModel.updatePost(
                id!!, name, indications, howToUse, price, typePost ?: "", imageUri
            )
        }
    }
}
