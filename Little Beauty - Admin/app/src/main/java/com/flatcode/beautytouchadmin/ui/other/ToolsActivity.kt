package com.flatcode.beautytouchadmin.ui.other

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
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.utils.Dialog as MyDialog
import com.flatcode.beautytouchadmin.databinding.ActivityToolsBinding
import com.canhub.cropper.CropImageContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToolsActivity : AppCompatActivity() {

    private var binding: ActivityToolsBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var imageUri2: Uri? = null
    private var imageUri3: Uri? = null
    private var imageUri4: Uri? = null
    private var dialog: Dialog? = null
    private val IMAGE_NOW = 1
    private val IMAGE_OLD = 2
    private val LOGO_NOW = 3
    private val LOGO_OLD = 4
    private var IMAGE_NUMBER = 0
    private val viewModel: ToolsViewModel by viewModels()

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
        cropImage.launch(cropImageSessionOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            when (IMAGE_NUMBER) {
                IMAGE_NOW -> {
                    imageUri = uri
                    binding!!.imageSessionNow.setImageURI(imageUri)
                }
                IMAGE_OLD -> {
                    imageUri2 = uri
                    binding!!.imageSessionOld.setImageURI(imageUri2)
                }
                LOGO_NOW -> {
                    imageUri3 = uri
                    binding!!.logoSessionNow.setImageURI(imageUri3)
                }
                LOGO_OLD -> {
                    imageUri4 = uri
                    binding!!.logoSessionOld.setImageURI(imageUri4)
                }
            }
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = MyDialog.loadingDialog(context)

        binding!!.editImageSessionNow.setOnClickListener {
            IMAGE_NUMBER = IMAGE_NOW
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editImageSessionOld.setOnClickListener {
            IMAGE_NUMBER = IMAGE_OLD
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editLogoSessionNow.setOnClickListener {
            IMAGE_NUMBER = LOGO_NOW
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editLogoSessionOld.setOnClickListener {
            IMAGE_NUMBER = LOGO_OLD
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.toolbar.nameSpace.setText(R.string.tools)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tools.collect { tools ->
                    tools?.let {
                        binding!!.imageSessionNow.loadImage(false, it.imageSession)
                        binding!!.imageSessionOld.loadImage(false, it.oldImageSession)
                        binding!!.logoSessionNow.loadImage(false, it.imageLogo)
                        binding!!.logoSessionOld.loadImage(false, it.oldImageLogo)
                        binding!!.sessionNow.setText(it.session)
                        binding!!.sessionOld.setText(it.oldSession)
                        binding!!.sessionNumberNow.setText(it.sessionNumber)
                        binding!!.sessionNumberOld.setText(it.oldSessionNumber)
                        binding!!.yearNow.setText(it.year)
                        binding!!.yearOld.setText(it.oldYear)
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
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val sessionNow = binding!!.sessionNow.text.toString().trim()
        val sessionOld = binding!!.sessionOld.text.toString().trim()
        val sessionNumberNow = binding!!.sessionNumberNow.text.toString().trim()
        val sessionNumberOld = binding!!.sessionNumberOld.text.toString().trim()
        val yearNow = binding!!.yearNow.text.toString().trim()
        val yearOld = binding!!.yearOld.text.toString().trim()

        if (sessionNow.isEmpty()) {
            Toast.makeText(context, "Enter the Session number", Toast.LENGTH_SHORT).show()
        } else if (sessionNumberNow.isEmpty()) {
            Toast.makeText(context, "Enter the Session name", Toast.LENGTH_SHORT).show()
        } else if (yearNow.isEmpty()) {
            Toast.makeText(context, "Enter the year", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Editing....")
            dialog!!.show()
            viewModel.updateTools(
                sessionNow, sessionOld, sessionNumberNow, sessionNumberOld, yearNow, yearOld,
                imageUri, imageUri2, imageUri3, imageUri4,
                imageUri?.getFileExtension(context),
                imageUri2?.getFileExtension(context),
                imageUri3?.getFileExtension(context),
                imageUri4?.getFileExtension(context)
            )
        }
    }
}
