package com.flatcode.beautytouch.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.beautytouch.databinding.ActivityProfileBinding
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.startCropActivity
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

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.let { intent ->
                    IntentCompat.getParcelableExtra(intent, "CROP_RESULT_URI", Uri::class.java)
                }
                binding?.image?.setImageURI(null)
                binding?.image?.setImageURI(imageUri)
                binding?.imageTrue?.visibility = View.VISIBLE
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                cropImageLauncher.launch(context.startCropActivity(it, 1, 1, true))
            }
        }

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

        @Suppress("DEPRECATION")
        dialog = ProgressDialog(context).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.editImageIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

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
                        dialog?.setMessage("The image is uploading...")
                        dialog?.show()
                    }
                    is Resource.Success -> {
                        dialog?.dismiss()
                    }
                    is Resource.Error -> {
                        dialog?.dismiss()
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
                        dialog?.setMessage("Saving changes...")
                        dialog?.show()
                    }
                    is Resource.Success -> {
                        dialog?.dismiss()
                        Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                        imageUri = null
                    }
                    is Resource.Error -> {
                        dialog?.dismiss()
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
        username = binding!!.nameEdit.text.toString().trim()
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(context, "Please enter the name", Toast.LENGTH_SHORT).show()
        } else {
            val uri = imageUri
            if (uri == null) {
                viewModel.updateProfile(username)
            } else {
                dialog?.setMessage("Uploading image...")
                dialog?.show()
                viewModel.uploadProfileImageCloudinary(uri) { uploadedUrl ->
                    if (uploadedUrl != null) {
                        viewModel.updateProfile(username, uploadedUrl)
                    } else {
                        dialog?.dismiss()
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        binding = null
    }
}
