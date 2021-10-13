package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.LocalUserStates
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditUserProfileImageViewModel(application: Application) : AndroidViewModel(application) {

    val userDataStore = UserDataStore(application)

    private val dialogConfiguration = DialogConfiguration()

    private val _userLocalState = MutableStateFlow<LocalUserStates>(LocalUserStates.Loading(isLoading = true))
    val userLocalState: StateFlow<LocalUserStates> = _userLocalState

    private val _uiState = MutableStateFlow<States>(States.Empty)
    val uiState :StateFlow<States> = _uiState

    //Get user local
    fun getLocalUser() {
        viewModelScope.launch {
            userDataStore
                .getUserFromDataStore()
                .collect {user->
                    if (user.imageUrl != "") {
                        _userLocalState.value = LocalUserStates.Success(localUser = user)
                    } else {
                        _userLocalState.value = LocalUserStates.EmptyProfileImage(localUser = user)
                    }
                }
        }
    }

    fun updateProfileImage(imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading

            //Upload image uri to firebase storage
            val storage = FirebaseStorage.getInstance().reference
            val storageReference = storage.child(FirebasePath.PROFILE_IMAGE_REF)
            storageReference
                .putFile(imageUri!!)
                .addOnCompleteListener{ task->
                    if (!task.isSuccessful) {
                        Log.d("EditImageFragment", "put image to storage failure exc-> ${task.exception?.message} ")
                        _uiState.value = States.Failure(task.exception?.message.toString())
                    }
                }

            Log.d("EditImageFragment", "before download url -> ${Thread.currentThread().name} ")
            storageReference.downloadUrl.addOnCompleteListener {task->
                if (task.isSuccessful) {
                    Log.d("EditImageFragment", "get image url currentThread -> ${Thread.currentThread().name} ")

                    val downloadlUrl = task.result
                    Log.d("EditImageFragment", "success get download url-> $downloadlUrl ")
                    viewModelScope.launch(Dispatchers.IO) {
                        uploadProfileImageToDatabase(downloadlUrl.toString())
                    }
                } else {
                    Log.d("EditImageFragment", "failure get download url exc-> ${task.exception?.message} ")
                }
            }
        }
    }

//    fun updateProfileImage(imageUri: Uri?) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _uiState.value = States.Loading
//
//            val uId = FirebaseAuth.getInstance().currentUser?.uid
//
//            val storageReference = FirebaseStorage
//                .getInstance()
//                .reference
//                .child(FirebasePath.PROFILE_IMAGE_REF)
//                .child(uId!!)
//
//
//        }
//    }

    //Push imageUrl to storage
    private fun uploadProfileImageToDatabase(imageUrl: String) {
        Log.d("EditImageFragment", "upload profile image url to realtime database currentThread -> ${Thread.currentThread().name} ")
        val uId = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserReference = FirebaseDatabase.getInstance()
            .reference
            .child(FirebasePath.USERS_REF)
            .child(uId!!)

        val profileImageUserReference = currentUserReference
            .child(FirebasePath.FIELD_PROFILE_IMAGE_URL)

        profileImageUserReference
            .setValue(imageUrl)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    _uiState.value = States.Success
                } else {
                    _uiState.value = States.Failure(task.exception?.message.toString())
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