package com.admin.project_kwork.models

data class ChatMessage(
    val messageId: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val messageText: String = ""
) {
    val timeSendMessage:Long = System.currentTimeMillis()
}