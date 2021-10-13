package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.ui.customviews.dialogs.LoadingProgressDialog
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.LocalUserStates
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditUserNameViewModel(application: Application) : AndroidViewModel(application) {

    val userDataStore = UserDataStore(application)

    private val dialogConfiguration = DialogConfiguration()

    private val _userLocalState = MutableStateFlow<LocalUserStates>(LocalUserStates.Loading(isLoading = true))
    val userLocalState:StateFlow<LocalUserStates> = _userLocalState

    private val _uiState = MutableStateFlow<States>(States.Empty)
    val uiState :StateFlow<States> = _uiState

    fun editUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading
            val uId = FirebaseAuth.getInstance().currentUser?.uid
            val currentUserReference = FirebaseDatabase.getInstance()
                .reference
                .child(FirebasePath.USERS_REF)
                .child(uId!!)
            val userNameReference = currentUserReference
                .child(FirebasePath.FIELD_NAME)
            userNameReference
                .setValue(name)
                .addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        _uiState.value = States.Success
                    } else {
                        _uiState.value = States.Failure(task.exception?.message.toString())
                    }
                }
        }
    }

    //Get user local from datastore
    fun getLocalUser() {
        viewModelScope.launch {
            userDataStore
                .getUserFromDataStore()
                .collect {user->
                    Log.d("EditNameFragment", "getLocalUser:user name -> ${user.userName} ")
                    if (user.userName != "") {
                        _userLocalState.value = LocalUserStates.Success(localUser = user)
                    } else {
                        _userLocalState.value = LocalUserStates.EmptyName(localUser = user)
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