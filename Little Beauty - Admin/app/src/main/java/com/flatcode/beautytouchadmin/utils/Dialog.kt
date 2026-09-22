package com.flatcode.beautytouchadmin.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.flatcode.beautytouchadmin.R

object Dialog {
    fun showDeleteDialog(context: Context, titleResId: Int, onYesClick: () -> Unit) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val title = dialog.findViewById<TextView>(R.id.title)
        title.setText(titleResId)

        dialog.findViewById<View>(R.id.yes).setOnClickListener {
            onYesClick()
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.no).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.attributes = lp
    }
}