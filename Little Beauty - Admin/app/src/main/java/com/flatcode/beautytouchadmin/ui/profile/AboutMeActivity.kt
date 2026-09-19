package com.flatcode.beautytouchadmin.ui.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.*
import com.flatcode.beautytouchadmin.ui.other.ToolsViewModel
import com.flatcode.beautytouchadmin.databinding.ActivityAboutMeBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutMeActivity : AppCompatActivity() {

    private var binding: ActivityAboutMeBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private val viewModel: ToolsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutMeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.toolbar.nameSpace.text = "About Me"
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }
        binding!!.show.setOnClickListener { showDialogAboutMy() }
        binding!!.editImageIcon.setOnClickListener { activity?.cropImageSquare() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tools.collect { tools ->
                    tools?.let {
                        binding!!.image.glide(true, it.imageMe)
                        binding!!.name.setText(it.aboutMe)
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
        val name = binding!!.name.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Image is being updated...")
            dialog!!.show()
            viewModel.updateAboutMe(name, imageUri, imageUri?.getFileExtension(context))
        }
    }

    private fun showDialogAboutMy() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_about)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val image = dialog.findViewById<ImageView>(R.id.image)
        val text = dialog.findViewById<TextView>(R.id.text)
        
        viewModel.tools.value?.let {
            image.glide(true, it.imageMe)
            text.text = it.aboutMe
        }
        
        dialog.show()
        dialog.window!!.attributes = lp
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                activity?.cropImageSquare()
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
