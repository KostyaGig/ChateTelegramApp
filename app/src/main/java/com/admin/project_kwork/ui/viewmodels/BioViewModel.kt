package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.LocalUserStates
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BioViewModel(application: Application) : AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val userDataStore = UserDataStore(application)
    val user = userDataStore.getUserFromDataStore().asLiveData()

    private val _localUserState = MutableStateFlow<LocalUserStates>(LocalUserStates.Loading(isLoading = true))
    val localUserState:StateFlow<LocalUserStates> = _localUserState

    fun getLocalUser() {
        viewModelScope.launch {
            userDataStore.getUserFromDataStore()
                .collect { localUser ->
                    Log.d("BioF", "getLocalUser: User image -> ${localUser.imageUrl}, name -> ${localUser.userName}")
                    if (localUser.userName != "" && localUser.imageUrl != "") {
                        _localUserState.value = LocalUserStates.Success(localUser = localUser)
                    } else if (localUser.userName != "" && localUser.imageUrl == "") {
                        _localUserState.value = LocalUserStates.EmptyProfileImage(localUser = localUser)
                    } else if (localUser.imageUrl != "" && localUser.userName == "") {
                        _localUserState.value = LocalUserStates.EmptyName(localUser = localUser)
                    } else {
                        if (localUser.email == "") {
                            Log.d("BioF","email empty")
                            _localUserState.value = LocalUserStates.EmptyBio(localUser = User(email = "yourEmail@mail.ru",password = "yourPassword"))
                        } else {
                            _localUserState.value = LocalUserStates.EmptyBio(localUser = localUser)
                        }

                    }
                    Log.d("BioFragment","locauserstate -> false")
                    _localUserState.value = LocalUserStates.Loading(isLoading = false)
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