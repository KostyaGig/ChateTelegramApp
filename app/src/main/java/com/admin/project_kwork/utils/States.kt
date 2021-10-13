package com.admin.project_kwork.utils

import com.admin.project_kwork.models.User

sealed class States {
    object Empty : States()
    object Loading : States()
    object Success : States()
    data class Failure(val message: String) : States()
}

sealed class LocalUserStates {
    data class Loading(val isLoading: Boolean): LocalUserStates()
    data class Success(val localUser: User): LocalUserStates()
    data class EmptyName(val localUser: User): LocalUserStates()
    data class EmptyProfileImage(val localUser: User): LocalUserStates()
    data class EmptyBio(val localUser: User): LocalUserStates()
    object EmptyDataStore: LocalUserStates()
}

//TODO Придумать норм название и описать для чего нужен данный state
sealed class State {
    data class SendMessage(val currentUser: User): State()
    object Empty: State()
}
