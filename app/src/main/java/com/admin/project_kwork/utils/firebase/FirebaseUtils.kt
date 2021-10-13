package com.admin.project_kwork.utils.firebase

object FirebasePath {
    //RealTDB path
    const val USERS_REF = "Users"
    const val FIELD_UID = "uid"
    const val FIELD_NAME = "userName"
    const val FIELD_EMAIL = "email"
    const val FIELD_PASSWORD = "password"
    const val FIELD_PROFILE_IMAGE_URL = "imageUrl"

    const val MESSAGES_REF = "Messages"
    const val SENDER_ID = "senderId"
    const val RECEIVER_ID = "receiverId"

    const val GROUPS_REF = "Groups"
    const val GROUP_MESSAGES_REF = "GroupMessages"
    const val JOINED_USERS = "JoinedUsers"

    const val MUSIC_REF = "Music"

    //FireStorage path
    const val PROFILE_IMAGE_REF = "profileImages"
}