package com.admin.project_kwork.ui.customviews.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.admin.project_kwork.R

class SaveLocalUserDialog(context: Context, val saveUser: (Boolean, Dialog) -> Unit) : Dialog(context), View.OnClickListener {

    private lateinit var saveUserBtn: Button
    private lateinit var cancelSaveUserBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.save_user_dialog_layout)
        saveUserBtn = findViewById(R.id.save_user_btn)
        cancelSaveUserBtn = findViewById(R.id.cancel_save_user_btn)

        saveUserBtn.setOnClickListener(this)
        cancelSaveUserBtn.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save_user_btn -> saveUser(true,this)
            R.id.cancel_save_user_btn -> saveUser(false,this)
        }
    }

}