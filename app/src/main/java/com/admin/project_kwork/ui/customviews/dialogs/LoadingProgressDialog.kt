package com.admin.project_kwork.ui.customviews.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import com.admin.project_kwork.R

class LoadingProgressDialog(context: Context) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loding_dialog_layout)
    }

    override fun onBackPressed() {
        if (this.isShowing) {
            super.onBackPressed()
        }
    }

}