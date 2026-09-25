package com.flatcode.beautytouch.utils

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.flatcode.beautytouch.databinding.DialogAboutBinding
import com.flatcode.beautytouch.databinding.DialogAppBinding
import com.flatcode.beautytouch.databinding.DialogCloseappBinding
import com.flatcode.beautytouch.databinding.DialogLoadingBinding
import com.flatcode.beautytouch.databinding.DialogLogoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoadingDialog(private val context: Context) {

    private var dialog: AlertDialog? = null
    private var binding: DialogLoadingBinding? = null

    fun show(message: String = "Please wait...") {
        if (dialog == null) {
            val binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
            this.binding = binding
            dialog = MaterialAlertDialogBuilder(context).setView(binding.root).setCancelable(false)
                .create()
        }
        binding?.messageTv?.text = message
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}

fun Activity.showCloseAppDialog(onYes: () -> Unit = { finish() }) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogCloseappBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.yes.setOnClickListener {
        onYes()
        alertDialog.dismiss()
    }

    dialogBinding.no.setOnClickListener { alertDialog.cancel() }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Activity.showDialogAboutMy(imageUrl: String?, aboutText: String?) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogAboutBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


    dialogBinding.image.loadImage(true, imageUrl)
    dialogBinding.text.text = aboutText

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Activity.showDialogAboutApp(
    onRate: () -> Unit = { rateUs() }, onDesignClick: () -> Unit, onProgrammerClick: () -> Unit
) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogAppBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.linearRate.setOnClickListener { onRate() }
    dialogBinding.facebookDesign.setOnClickListener { onDesignClick() }
    dialogBinding.facebookProgrammer.setOnClickListener { onProgrammerClick() }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Activity.showDialogLogout(onLogout: () -> Unit) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.yes.setOnClickListener {
        onLogout()
        alertDialog.dismiss()
    }
    dialogBinding.no.setOnClickListener { alertDialog.cancel() }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}