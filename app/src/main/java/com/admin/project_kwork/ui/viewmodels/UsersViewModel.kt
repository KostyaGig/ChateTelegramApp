package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.ui.customviews.dialogs.ProfileUserDialog
import com.admin.project_kwork.ui.customviews.dialogs.dialogListeners.ProfileUserDialogClickListener
import com.admin.project_kwork.utils.State
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsersViewModel(application: Application) : AndroidViewModel(application),ProfileUserDialogClickListener {

    private val dialogConfiguration = DialogConfiguration(null,this)

    private val _uiState = MutableStateFlow<States>(States.Loading)
    val uiState: StateFlow<States> = _uiState

    private val _sendMessageState = MutableStateFlow<State>(State.Empty)
    val sendMessageState: StateFlow<State> = _sendMessageState

    fun currentUID () = FirebaseAuth.getInstance().currentUser?.uid

    fun setEmptyUISate() {
        _uiState.value = States.Empty
    }

    fun setEmptySendMessageState() {
        _sendMessageState.value = State.Empty
    }

    override fun onSendMessageBtnClick(dialog: Dialog, currentUser: User) {
        dialog.dismiss()
        _sendMessageState.value = State.SendMessage(currentUser = currentUser)
    }

    fun createProfileUserDialog(context: Context,currentUser: User) {
        dialogConfiguration.createProfileUserDialog(context = context,currentUser = currentUser)
    }

    fun showProfileUserDialog() {
        dialogConfiguration.showDialog()
    }

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissDialog() {
        dialogConfiguration.hideDialog()
    }

}