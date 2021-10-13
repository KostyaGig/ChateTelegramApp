package com.admin.project_kwork.ui.customviews.dialogs.dialogListeners

import android.app.Dialog

interface ProfileUserDialogClickListener {
    fun onSendMessageBtnClick(dialog: Dialog,currentUser: com.admin.project_kwork.models.User)
}