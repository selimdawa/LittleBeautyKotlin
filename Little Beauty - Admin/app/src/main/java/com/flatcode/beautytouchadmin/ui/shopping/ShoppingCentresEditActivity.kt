package com.flatcode.beautytouchadmin.ui.shopping

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
import com.flatcode.beautytouchadmin.model.ShoppingCenter
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.databinding.ActivityShoppingCentersAddBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCentresEditActivity : AppCompatActivity() {

    private var binding: ActivityShoppingCentersAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var id: String? = null
    private var imageUri: Uri? = null
    private var imageUri2: Uri? = null
    private var dialog: ProgressDialog? = null
    private val IMAGE_PIC = 1
    private val IMAGE_MAP = 2
    private var IMAGE_NUMBER = 0
    private val viewModel: ShoppingActionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCentersAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        id = intent.getStringExtra(DATA.SHOPPING_CENTER_ID)

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.addImage.setOnClickListener {
            activity?.cropImageShoppingCenter()
            IMAGE_NUMBER = IMAGE_PIC
        }
        binding!!.addImageTwo.setOnClickListener {
            activity?.cropImageShoppingCenter()
            IMAGE_NUMBER = IMAGE_MAP
        }

        binding!!.toolbar.nameSpace.text = "Edit the shopping center"
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }

        id?.let { viewModel.loadCenter(it) }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.center.collect { item ->
                    item?.let {
                        binding!!.name.setText(it.name)
                        binding!!.location.setText(it.location)
                        binding!!.location2.setText(it.location2)
                        binding!!.location3.setText(it.location3)
                        binding!!.numberPhone.setText(it.numberPhone)
                        binding!!.imageOne.glide(false, it.imageurl)
                        binding!!.imageTwo.glide(false, it.imageurl2)
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
        val locationOne = binding!!.location.text.toString().trim()
        val locationTwo = binding!!.location2.text.toString().trim()
        val locationThree = binding!!.location3.text.toString().trim()
        val numberPhone = binding!!.numberPhone.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter the name of the center", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(locationOne)) {
            Toast.makeText(context, "Enter the city name", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(locationTwo)) {
            Toast.makeText(context, "Enter the name of the neighborhood", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(locationThree)) {
            Toast.makeText(context, "Enter the street name", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(numberPhone)) {
            Toast.makeText(context, "Enter a phone number", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Editing....")
            dialog!!.show()
            viewModel.updateShoppingCenter(
                id!!, name, locationOne, locationTwo, locationThree, numberPhone,
                imageUri, imageUri2,
                imageUri?.getFileExtension(context),
                imageUri2?.getFileExtension(context)
            )
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                if (IMAGE_NUMBER == IMAGE_PIC) {
                    imageUri = uri
                } else if (IMAGE_NUMBER == IMAGE_MAP) {
                    imageUri2 = uri
                }
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                activity?.cropImageShoppingCenter()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                if (IMAGE_NUMBER == IMAGE_PIC) {
                    imageUri = result.uri
                    binding!!.imageOne.setImageURI(imageUri)
                } else if (IMAGE_NUMBER == IMAGE_MAP) {
                    imageUri2 = result.uri
                    binding!!.imageTwo.setImageURI(imageUri2)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
