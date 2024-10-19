package com.services.provider.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.services.provider.databinding.DialogProgressBinding


object ProgressDialogUtil {

    private var progressDialog: Dialog? = null
    fun showProgressDialog(context: Context?) {
        if (context == null)
            return
        val builder =
            Dialog(context)
        builder.setCancelable(false)

        progressDialog = builder
        builder.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            val binding = DialogProgressBinding.inflate(LayoutInflater.from(context))
            setContentView(binding.root)
            progressDialog?.show()
        }

    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }
}