package com.admin.project_kwork.models

import android.os.Parcelable
import java.io.Serializable

data class Group(
    val groupId: String = "",
    val adminId: String = "",
    val title: String = ""
): Serializable {
    val timeCreatedGroup:Long = System.currentTimeMillis()
}