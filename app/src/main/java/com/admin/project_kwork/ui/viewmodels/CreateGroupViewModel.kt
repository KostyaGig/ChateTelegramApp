package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.models.Group
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateGroupViewModel(application: Application): AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val _uiState = MutableStateFlow<States>(States.Empty)
    val uiState: StateFlow<States> = _uiState

    fun createGroup(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading
            val groupsReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.GROUPS_REF)

            val uId = FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid

            //generate group id
            val groupId = groupsReference.push().key

            val group = Group(groupId = groupId!!,adminId = uId!!,title = title)
            groupsReference
                .child(groupId)
                .setValue(group)
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        _uiState.value = States.Success
                    } else {
                        _uiState.value = States.Failure(message = task.exception?.message.toString())
                    }
                }
        }
    }


    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissLoadingProgressDialog() {
        dialogConfiguration.hideDialog()
    }

}