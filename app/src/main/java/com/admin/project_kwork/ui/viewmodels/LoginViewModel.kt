package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val userDataStore = UserDataStore(application)
    private val currentUser = userDataStore.getUserFromDataStore()

    private val _uiState = MutableStateFlow<States>(States.Empty)
    val uiState : StateFlow<States> = _uiState

    fun login (email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading
            if (email.isEmpty() || password.isEmpty()) {
                Log.d("LoginFragment","login viewmodel null")
                _uiState.value = States.Failure("null")
            } else {
                currentUser.collect {localUser->
                    if (localUser.email != "") {
                        if (localUser.email == email && localUser.password == password) {
                            signInLocalUser(localUser)
                        } else {
                            _uiState.value = States.Failure("Если эта почта не зарегистрирована,нажмите на кнопку \"Сброс\" и повторите еще раз")
                        }
                    } else {
                        //Иначе заходим через почту
                        val mAuth = FirebaseAuth.getInstance()
                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                                task ->
                            if (task.isSuccessful) {
                                pushUserToDatabase(email, password)
                            } else {
                                _uiState.value = States.Failure(task.exception?.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    fun signInLocalUser(localUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("LoginF","Local sign in")
            //Заходим в локального юзера
            _uiState.value = States.Loading
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithEmailAndPassword(localUser.email,localUser.password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    _uiState.value = States.Success
                } else {
                    _uiState.value = States.Failure(task.exception?.message.toString())
                }
            }
        }
    }

    fun pushUserToDatabase(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mAuth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance().reference.child(FirebasePath.USERS_REF)
            val uId = mAuth.currentUser!!.uid
            val user = User(uid = uId,email = email,password = password)
            database.child(uId).setValue(user).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    _uiState.value = States.Success
                } else {
                    _uiState.value = States.Failure(task.exception?.message.toString())
                }
            }
        }
    }

    fun existUser(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun resetUser() {
        userDataStore.resetUser()
    }

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissLoadingProgressDialog() {
        dialogConfiguration.hideDialog()
    }
}

