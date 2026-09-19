package com.flatcode.beautytouchadmin.ui.post

import android.Manifest
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
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.cropImageSquare
import com.flatcode.beautytouchadmin.utils.getFileExtension
import com.flatcode.beautytouchadmin.databinding.ActivityPostAddBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostAddActivity : AppCompatActivity() {

    private var binding: ActivityPostAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var typePost = DATA.EMPTY
    private val viewModel: PostActionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

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
        binding!!.go.setOnClickListener { validateData() }
        binding!!.layoutImageProfile.setOnClickListener { activity!!.cropImageSquare() }

        observeViewModel()
    }

    private fun observeViewModel() {
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
        } else if (typePost == DATA.EMPTY) {
            Toast.makeText(context, "Enter a product category", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, "There's no picture!", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Post being created...")
            dialog!!.show()
            viewModel.addPost(
                name, indications, howToUse, price, typePost, imageUri!!,
                imageUri!!.getFileExtension(context)!!
            )
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                activity!!.cropImageSquare()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
