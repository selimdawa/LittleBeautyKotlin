package com.flatcode.littlebeautyadmin.ui.shopping

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlebeautyadmin.R
import com.flatcode.littlebeautyadmin.databinding.ActivityShoppingCentersAddBinding
import com.flatcode.littlebeautyadmin.utils.BaseActivity
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.ProgressDialog
import com.flatcode.littlebeautyadmin.utils.checkStoragePermission
import com.flatcode.littlebeautyadmin.utils.cropImage
import com.flatcode.littlebeautyadmin.utils.loadImage
import com.flatcode.littlebeautyadmin.utils.pickImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCentresEditActivity : BaseActivity() {

    private var binding: ActivityShoppingCentersAddBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var id: String? = null
    private var imageUri: Uri? = null
    private var imageUri2: Uri? = null
    private var isDataLoaded = false
    private var dialog: ProgressDialog? = null
    private val imagePic = 1
    private val imageMap = 2
    private var imageNumber = 0
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
        pickImage(DATA.MIN_SLIDER_X)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA.MIN_SLIDER_X) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data
                if (uri != null) {
                    cropImage(
                        uri = uri,
                        aspectRatioX = 2,
                        aspectRatioY = 1,
                        isOval = false,
                        minWidth = DATA.MIN_SLIDER_X,
                        minHeight = DATA.MIN_SLIDER_Y,
                        requestCode = DATA.MIN_SLIDER_X
                    )
                } else {
                    val resultUri =
                        IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                    if (resultUri != null) {
                        if (imageNumber == imagePic) {
                            imageUri = resultUri
                            binding!!.imageOne.setImageURI(imageUri)
                        } else if (imageNumber == imageMap) {
                            imageUri2 = resultUri
                            binding!!.imageTwo.setImageURI(imageUri2)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCentersAddBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        id = intent.getStringExtra(DATA.SHOPPING_CENTER_ID)

        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("IS_DATA_LOADED", false)
            imageNumber = savedInstanceState.getInt("IMAGE_NUMBER", 0)
            imageUri = BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI", Uri::class.java)
            imageUri2 = BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI_2", Uri::class.java)
            imageUri?.let { binding!!.imageOne.setImageURI(it) }
            imageUri2?.let { binding!!.imageTwo.setImageURI(it) }
        }

        dialog = ProgressDialog(context).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.addImage.setOnClickListener {
            imageNumber = imagePic
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.addImageTwo.setOnClickListener {
            imageNumber = imageMap
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        binding!!.toolbar.nameSpace.setText(R.string.edit_shopping_center)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }

        id?.let { viewModel.loadCenter(it) }
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_DATA_LOADED", isDataLoaded)
        outState.putInt("IMAGE_NUMBER", imageNumber)
        imageUri?.let { outState.putParcelable("IMAGE_URI", it) }
        imageUri2?.let { outState.putParcelable("IMAGE_URI_2", it) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.center.collect { item ->
                    item?.let {
                        if (!isDataLoaded) {
                            binding!!.name.setText(it.name)
                            binding!!.location.setText(it.location)
                            binding!!.location2.setText(it.location2)
                            binding!!.location3.setText(it.location3)
                            binding!!.numberPhone.setText(it.numberPhone)
                            binding!!.imageOne.loadImage(false, it.imageurl)
                            binding!!.imageTwo.loadImage(false, it.imageurl2)
                            isDataLoaded = true
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog?.dismiss()
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
        } else {
            dialog?.setMessage(getString(R.string.loading))
            dialog?.show()
            viewModel.updateShoppingCenter(
                id!!,
                name,
                locationOne,
                locationTwo,
                locationThree,
                numberPhone,
                imageUri,
                imageUri2
            )
        }
    }
}