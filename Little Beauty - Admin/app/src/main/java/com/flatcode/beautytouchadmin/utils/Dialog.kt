package com.flatcode.beautytouchadmin.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.flatcode.beautytouchadmin.R

object Dialog {
    fun showDeleteDialog(context: Context, titleResId: Int, onYesClick: () -> Unit) {
        Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_logout)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val lp = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }

            findViewById<TextView>(R.id.title).setText(titleResId)

            findViewById<TextView>(R.id.yes).setOnClickListener {
                onYesClick()
                dismiss()
            }
            findViewById<TextView>(R.id.no).setOnClickListener {
                dismiss()
            }
            show()
            window?.attributes = lp
        }
    }

    fun loadingDialog(context: Context): Dialog {
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}

fun Dialog.setMessage(message: String) {
    findViewById<TextView>(R.id.message)?.text = message
}