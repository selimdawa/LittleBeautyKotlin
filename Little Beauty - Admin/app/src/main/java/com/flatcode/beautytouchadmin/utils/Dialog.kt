package com.flatcode.beautytouchadmin.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.DialogLoadingBinding
import com.flatcode.beautytouchadmin.databinding.DialogLogoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Activity.showDeleteDialog(titleResId: Int, onYesClick: () -> Unit) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.title.setText(titleResId)

    dialogBinding.yes.setOnClickListener {
        onYesClick()
        alertDialog.dismiss()
    }
    dialogBinding.no.setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun loadingDialog(context: Context): Dialog {
    val binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
    return Dialog(context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCancelable(false)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }
}

fun Dialog.setMessage(message: String) {
    findViewById<TextView>(R.id.message)?.text = message
}