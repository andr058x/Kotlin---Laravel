package com.raj.weathertodo.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.raj.weathertodo.R

class ProgressDialog(context: Context) {
    var d: Dialog = Dialog(context)
    var v: View = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false)

    init {
        d.setContentView(v)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

    }

    fun show() {
        d.show()
    }

    fun dismiss() {
        d.dismiss()
    }
}