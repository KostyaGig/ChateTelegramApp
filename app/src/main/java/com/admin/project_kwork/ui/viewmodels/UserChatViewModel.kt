package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.models.ChatMessage
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserChatViewModel(application: Application) : AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val _uiState = MutableStateFlow<States>(States.Loading)
    val uiState:StateFlow<States> = _uiState

    private var clickedUser: User? = User()

    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO ){
            val messageReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.MESSAGES_REF)

            val currentFriendMessageReference = messageReference
                .child(clickedUser!!.uid)

            //message id
            val messageId = currentFriendMessageReference.push().key

            val message = ChatMessage(messageId!!,getClickedUser()!!.uid,currentUID()!!,text)
            currentFriendMessageReference
                .child(messageId)
                .setValue(message)
        }
    }

    fun deleteMessage(message: ChatMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            val messageReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.MESSAGES_REF)

            val currentMessageReference = messageReference
                .child(message.receiverId)
                .child(message.messageId)

            currentMessageReference.removeValue()
        }
    }

    fun currentUID () = FirebaseAuth.getInstance().currentUser?.uid

    fun setClickedUser(clickedUser: User?) {
        this.clickedUser = clickedUser
    }

    fun getClickedUser(): User? {
        return clickedUser
    }

    fun setEmptySate() {
        _uiState.value = States.Empty
    }

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissLoadingProgressDialog() {
        dialogConfiguration.hideDialog()
    }

}