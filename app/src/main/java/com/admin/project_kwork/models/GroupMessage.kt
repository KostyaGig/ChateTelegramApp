package com.admin.project_kwork.models

data class GroupMessage(
    val groupId: String = "",
    val senderId: String = "",
    val messageText: String = ""
) {
    val timeSendMessage:Long = System.currentTimeMillis()
}
