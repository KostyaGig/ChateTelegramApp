package com.admin.project_kwork.ui.customviews.dialogs

import android.app.Application
import android.app.Dialog
import android.content.Context
import com.admin.project_kwork.ui.customviews.dialogs.dialogListeners.ProfileUserDialogClickListener
import com.admin.project_kwork.ui.customviews.dialogs.dialogListeners.SaveLocalUserClickListener

class DialogConfiguration(
    private val saveLocalUserClickListener: SaveLocalUserClickListener? = null,
    private val profileUserDialogClickListener: ProfileUserDialogClickListener? = null
) {

    private lateinit var currentDialog: Dialog

    fun createProfileUserDialog(context: Context,currentUser: com.admin.project_kwork.models.User){
        currentDialog = ProfileUserDialog(context) {dialog->
            profileUserDialogClickListener?.onSendMessageBtnClick(dialog,currentUser)
        }
        currentDialog.create()
        (currentDialog as ProfileUserDialog).setUserProfile(currentUser = currentUser)
    }

    fun createSaveLocalUserDialog(context: Context) {
        currentDialog = SaveLocalUserDialog(context) {save,dialog ->
            saveLocalUserClickListener?.onSavedLocalUserBtnClickListener(save,dialog)
        }
    }

    fun createLoadingProgressDialog(context: Context) {
        currentDialog = LoadingProgressDialog(context)
    }

    fun showDialog() {
        currentDialog.show()
    }

    fun hideDialog() {
        currentDialog.dismiss()
    }

}
