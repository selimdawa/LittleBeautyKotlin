package com.flatcode.beautytouchadmin.ui.post

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.databinding.ActivityPostAddBinding
import com.canhub.cropper.CropImageContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostEditActivity : AppCompatActivity() {

    private var binding: ActivityPostAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var id: String? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var typePost: String? = DATA.EMPTY
    private val viewModel: PostActionViewModel by viewModels()

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

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.toolbar.nameSpace.setText(R.string.add_post)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        binding!!.typeOne.setOnClickListener {
            typePost = "Skin Products"
            binding!!.typeOne.text = "Skin Products ✓"
            binding!!.typeTwo.text = "Hair Products"
        }
        binding!!.typeTwo.setOnClickListener {
            typePost = "Hair Products"
            binding!!.typeOne.text = "Skin Products"
            binding!!.typeTwo.text = "Hair Products ✓"
        }

        binding!!.toolbar.nameSpace.text = "Edit post"
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }
        binding!!.layoutImageProfile.setOnClickListener { cropImage.launch(cropImageSquareOptions()) }

        id?.let { viewModel.loadPost(it) }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { item ->
                    item?.let {
                        typePost = it.category
                        binding!!.image.glide(true, it.postimage)
                        binding!!.name.setText(it.name)
                        binding!!.price.setText(it.price)
                        binding!!.indications.setText(it.indications)
                        binding!!.howToUse.setText(it.use)
                        if (it.category == "Skin Products") {
                            binding!!.typeOne.text = "Skin Products ✓"
                            binding!!.typeTwo.text = "Hair Products"
                        } else if (it.category == "Hair Products") {
                            binding!!.typeOne.text = "Skin Products"
                            binding!!.typeTwo.text = "Hair Products ✓"
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

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(indications)) {
            Toast.makeText(context, "Enter the indications", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(howToUse)) {
            Toast.makeText(context, "Enter how to use", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(context, "Enter a price", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Post is updating")
            dialog!!.show()
            viewModel.updatePost(
                id!!, name, indications, howToUse, price, typePost ?: "",
                imageUri, imageUri?.getFileExtension(context)
            )
        }
    }
}
