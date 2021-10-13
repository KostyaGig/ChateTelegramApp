package com.admin.project_kwork.models

import android.os.Parcelable
import java.io.Serializable

data class  User(
    val uid: String = "",
    val email: String = "",
    val userName: String = "",
    val password: String = "",
    val imageUrl: String = ""
): Serializable {

}