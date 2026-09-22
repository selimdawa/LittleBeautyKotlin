package com.flatcode.beautytouchadmin.ui.shopping

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.utils.Dialog as MyDialog
import com.flatcode.beautytouchadmin.databinding.ActivityShoppingCentersAddBinding
import com.canhub.cropper.CropImageContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCentersAddActivity : AppCompatActivity() {

    private var binding: ActivityShoppingCentersAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var imageUri2: Uri? = null
    private var dialog: Dialog? = null
    private val IMAGE_PIC = 1
    private val IMAGE_MAP = 2
    private var IMAGE_NUMBER = 0
    private val viewModel: ShoppingActionViewModel by viewModels()

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
        cropImage.launch(cropImageShoppingCenterOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (IMAGE_NUMBER == IMAGE_PIC) {
                imageUri = uri
                binding!!.imageOne.setImageURI(imageUri)
            } else if (IMAGE_NUMBER == IMAGE_MAP) {
                imageUri2 = uri
                binding!!.imageTwo.setImageURI(imageUri2)
            }
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCentersAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = MyDialog.loadingDialog(context)

        binding!!.addImage.setOnClickListener {
            IMAGE_NUMBER = IMAGE_PIC
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.addImageTwo.setOnClickListener {
            IMAGE_NUMBER = IMAGE_MAP
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        binding!!.toolbar.nameSpace.text = "Add a shopping center"
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }

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
        val locationOne = binding!!.location.text.toString().trim()
        val locationTwo = binding!!.location2.text.toString().trim()
        val locationThree = binding!!.location3.text.toString().trim()
        val numberPhone = binding!!.numberPhone.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, "Enter the name of the center", Toast.LENGTH_SHORT).show()
        } else if (locationOne.isEmpty()) {
            Toast.makeText(context, "Enter the city name", Toast.LENGTH_SHORT).show()
        } else if (locationTwo.isEmpty()) {
            Toast.makeText(context, "Enter the name of the neighborhood", Toast.LENGTH_SHORT).show()
        } else if (locationThree.isEmpty()) {
            Toast.makeText(context, "Enter the street name", Toast.LENGTH_SHORT).show()
        } else if (numberPhone.isEmpty()) {
            Toast.makeText(context, "Enter a phone number", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, "There is no center image!", Toast.LENGTH_SHORT).show()
        } else if (imageUri2 == null) {
            Toast.makeText(context, "There is no location picture!", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Shopping center under construction...")
            dialog!!.show()
            viewModel.addShoppingCenter(
                name, locationOne, locationTwo, locationThree, numberPhone,
                imageUri!!, imageUri2!!,
                imageUri?.getFileExtension(context)!!,
                imageUri2?.getFileExtension(context)!!
            )
        }
    }
}
