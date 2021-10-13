package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.ui.customviews.dialogs.LoadingProgressDialog
import com.admin.project_kwork.ui.customviews.dialogs.SaveLocalUserDialog
import com.admin.project_kwork.ui.customviews.dialogs.dialogListeners.SaveLocalUserClickListener
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.LocalUserStates
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application),SaveLocalUserClickListener {

    private val dialogConfiguration = DialogConfiguration(this)

    private val userDataStore:UserDataStore = UserDataStore(application)

    private val _localUserState = MutableStateFlow<LocalUserStates>(LocalUserStates.Loading(isLoading = true))
    val localUserState: StateFlow<LocalUserStates> = _localUserState

    //TODO Изменить код разбить на метод
    fun getUserFromFirebaseDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val uId = FirebaseAuth.getInstance().currentUser?.uid
            val userReference = FirebaseDatabase.getInstance().reference.child(FirebasePath.USERS_REF).child(uId!!)
                userReference.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("ChatsFragment"," MainVewModel Error -> ${p0.message}")
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //Инкогда данные синхронизируютсся сами когда этот метод вызывается,иногда вручную надо
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            Log.d("ChatsFragment","MainViewModel user email-> ${user.email}")
                            saveUserToDataStore(user = user)
                        }
                    }
                })
        }
    }

    //TODO Добавить проверку успешного или неудачного сохранения юзера в бд
    fun saveUserToDataStore(user: User) {
        userDataStore.saveUserToDataStore(user)
    }

    //Get user from data store
    fun getLocalUser() {
        Log.d("ChatsFragment", "getLocalUser: ")
        viewModelScope.launch {
            userDataStore.getUserFromDataStore()
                .collect { localUser ->
                    if (localUser.email != "") {
                        Log.d("ChatsFragment", "MainViewMoel Collect")
                        if (localUser.userName != "" && localUser.imageUrl != "") {
                            _localUserState.value = LocalUserStates.Success(localUser = localUser)
                        } else if (localUser.userName != "" && localUser.imageUrl == "") {
                            _localUserState.value = LocalUserStates.EmptyProfileImage(localUser = localUser)
                        } else if (localUser.imageUrl != "" && localUser.userName == "") {
                            _localUserState.value = LocalUserStates.EmptyName(localUser = localUser)
                        } else {
                            _localUserState.value = LocalUserStates.EmptyBio(localUser = localUser)
                        }
                    } else {
                        _localUserState.value = LocalUserStates.EmptyDataStore
                    }
                    _localUserState.value = LocalUserStates.Loading(isLoading = false)
                }
            }
        }

    fun existFromAccount() {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
    }

    fun showSaveUserDialog(context: Context) {
        dialogConfiguration.createSaveLocalUserDialog(context)
        dialogConfiguration.showDialog()
    }

    override fun onSavedLocalUserBtnClickListener(isSave: Boolean, dialog: Dialog) {
        Log.d("Dialog","MainViewmodel click save local user")
        if (isSave) {
            getUserFromFirebaseDatabase()
        }
        dialog.dismiss()
    }

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissLoadingProgressDialog() {
        dialogConfiguration.hideDialog()
    }
}