package com.flatcode.beautytouch.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.flatcode.beautytouch.databinding.DialogLoadingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoadingDialog(private val context: Context) {

    private var dialog: AlertDialog? = null
    private var binding: DialogLoadingBinding? = null

    fun show(message: String = "Please wait...") {
        if (dialog == null) {
            val binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
            this.binding = binding
            dialog = MaterialAlertDialogBuilder(context)
                .setView(binding.root)
                .setCancelable(false)
                .create()
        }
        binding?.messageTv?.text = message
        dialog?.show()
    }

    fun setMessage(message: String) {
        binding?.messageTv?.text = message
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}