package com.flatcode.beautytouch.ui.profile

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.CropImageSquare
import com.flatcode.beautytouch.utils.getFileExtension
import com.flatcode.beautytouch.databinding.ActivityProfileBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        binding!!.back.setOnClickListener { onBackPressed() }
        binding!!.editImageIcon.setOnClickListener { activity.CropImageSquare() }

        binding!!.imageEdit.setOnClickListener {
            binding!!.imageEdit.visibility = View.GONE
            binding!!.imageClose.visibility = View.VISIBLE
            binding!!.imageTrue.visibility = View.VISIBLE
            binding!!.name.visibility = View.GONE
            binding!!.nameEdit.visibility = View.VISIBLE
        }
        binding!!.imageClose.setOnClickListener {
            binding!!.imageEdit.visibility = View.VISIBLE
            binding!!.imageClose.visibility = View.GONE
            if (imageUri != null) {
                binding!!.imageTrue.visibility = View.VISIBLE
            } else {
                binding!!.imageTrue.visibility = View.GONE
            }
            binding!!.name.visibility = View.VISIBLE
            binding!!.nameEdit.visibility = View.GONE
        }
        binding!!.imageTrue.setOnClickListener {
            binding!!.imageClose.visibility = View.GONE
            binding!!.imageEdit.visibility = View.VISIBLE
            binding!!.imageTrue.visibility = View.GONE
            binding!!.name.visibility = View.VISIBLE
            binding!!.name.text = binding!!.nameEdit.text.toString()
            binding!!.nameEdit.visibility = View.GONE
            validateData()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userInfo.collect { resource ->
                Timber.d("User info collected: $resource")
                if (resource is Resource.Success) {
                    val user = resource.data
                    binding!!.image.load(user.imageurl)
                    binding!!.name.text = user.username
                    binding!!.nameEdit.setText(user.username)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uploadImageState.collect { resource ->
                Timber.d("Upload image state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog!!.setMessage("The image is loading...")
                        dialog!!.show()
                    }
                    is Resource.Success -> {
                        viewModel.updateProfile(username, resource.data)
                    }
                    is Resource.Error -> {
                        dialog!!.dismiss()
                        Timber.e("Upload image error: ${resource.message}")
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateProfileState.collect { resource ->
                Timber.d("Update profile state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog!!.setMessage("Modifications are loaded...")
                        dialog!!.show()
                    }
                    is Resource.Success -> {
                        dialog!!.dismiss()
                        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                        imageUri = null
                    }
                    is Resource.Error -> {
                        dialog!!.dismiss()
                        Timber.e("Update profile error: ${resource.message}")
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private var username = DATA.EMPTY
    private fun validateData() {
        username = binding!!.nameEdit.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Please enter the name", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                viewModel.updateProfile(username)
            } else {
                viewModel.uploadProfileImage(imageUri!!, imageUri.getFileExtension(context)!!)
            }
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
                activity.CropImageSquare()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                binding!!.image.setImageURI(imageUri)
                binding!!.imageTrue.visibility = View.VISIBLE
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
                binding!!.imageTrue.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        viewModel.loadUserInfo()
        super.onResume()
    }
}