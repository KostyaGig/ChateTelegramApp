package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.utils.States
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GroupsViewModel(application: Application) : AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val _uiState = MutableStateFlow<States>(States.Loading)
    val uiState: StateFlow<States> = _uiState

    private val _groupsCount = MutableStateFlow<Int>(0)
    val groupsCount: StateFlow<Int>
        get() = _groupsCount

    fun currentUID() = FirebaseAuth.getInstance().currentUser?.uid

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissDialog() {
        dialogConfiguration.hideDialog()
    }

    fun setEmptyUIState() {
        _uiState.value = States.Empty
    }

    fun changeCountGroups(countGroups: Int) {
        _groupsCount.value = countGroups
    }
}