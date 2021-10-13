package com.admin.project_kwork.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.admin.project_kwork.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class UserDataStore(context: Context) {

    private val TAG = "DataStore"

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val dataStore = context.createDataStore(
        ConfigDataStore.NAME_DATA_STORE,
        corruptionHandler = null,
        scope = scope
    )

    fun saveUserToDataStore(user: User){
        Log.d("SaveD","Save")
        scope.launch {
            dataStore.edit {prefs ->

                prefs[DataKeys.USER_ID] = user.uid
                prefs[DataKeys.USER_NAME] = user.userName
                prefs[DataKeys.USER_EMAIL] = user.email
                prefs[DataKeys.USER_PASSWORD] = user.password
                prefs[DataKeys.USER_PROFILE_IMAGE_URL] = user.imageUrl
            }
        }
    }

    fun getUserFromDataStore(): kotlinx.coroutines.flow.Flow<User>{
      return dataStore.data
            .map { prefs ->
                val uId = prefs[DataKeys.USER_ID] ?: ""
                val userName = prefs[DataKeys.USER_NAME] ?: ""
                val email = prefs[DataKeys.USER_EMAIL] ?: ""
                val password = prefs[DataKeys.USER_PASSWORD] ?: ""
                val imageUrl = prefs[DataKeys.USER_PROFILE_IMAGE_URL] ?: ""
                User(uId,email,userName,password,imageUrl)
            }
    }

    fun resetUser() {
        scope.launch {
            dataStore.edit {prefs->
                prefs[DataKeys.USER_ID] = ""
                prefs[DataKeys.USER_NAME] = ""
                prefs[DataKeys.USER_EMAIL] = ""
                prefs[DataKeys.USER_PASSWORD] = ""
                prefs[DataKeys.USER_PROFILE_IMAGE_URL] = ""
            }
        }
    }

    fun shutDownJob() {
        job.cancel()
    }

}

object DataKeys {
    val USER_ID = preferencesKey<String>("uId")
    val USER_NAME = preferencesKey<String>("userName")
    val USER_EMAIL = preferencesKey<String>("userEmail")
    val USER_PASSWORD = preferencesKey<String>("userPassword")
    val USER_PROFILE_IMAGE_URL = preferencesKey<String>("userProfileImageUrl")
}
object ConfigDataStore{
    const val NAME_DATA_STORE = "user data store"
}