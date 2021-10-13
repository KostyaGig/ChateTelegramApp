package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.data.local.datastore.UserDataStore
import com.admin.project_kwork.models.User
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SyncronizedUserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDataStore = UserDataStore(application)
    val currentUser = userDataStore.getUserFromDataStore()

    private val _uiState = MutableStateFlow<States>(States.Empty)
    val uiState: StateFlow<States> = _uiState

    fun syncronizedData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading
            currentUser.collect { localUser ->
                getUserFromDatabase(localUser = localUser)
            }
        }
    }

    fun getUserFromDatabase(localUser: User) {
        Log.d("SyncronizedFragment","Get user from database current thread -> ${Thread.currentThread().name}")
        val uId = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserReference = FirebaseDatabase
            .getInstance()
            .reference
            .child(FirebasePath.USERS_REF)
            .child(uId!!)

        currentUserReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                _uiState.value = States.Failure(error.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    if (
                        localUser.userName == user.userName &&
                        localUser.email == user.email &&
                        localUser.imageUrl == user.imageUrl &&
                        localUser.password == user.password

                    ) {
                        _uiState.value = States.Success
                    } else {
                        _uiState.value = States.Failure("Actual")
                    }
                }
            }
        })
    }

}