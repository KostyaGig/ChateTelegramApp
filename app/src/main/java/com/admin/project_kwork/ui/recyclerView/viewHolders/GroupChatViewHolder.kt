package com.admin.project_kwork.ui.recyclerView.viewHolders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.ChatMessage
import com.admin.project_kwork.models.Group
import com.admin.project_kwork.models.GroupMessage
import com.admin.project_kwork.models.User
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.google.firebase.database.*

class GroupFriendViewHolder(view: View, private val onLongClick:(GroupMessage) -> Boolean) : RecyclerView.ViewHolder(view) {

    private val text = itemView.findViewById<TextView>(R.id.text)
    private val senderNameText= itemView.findViewById<TextView>(R.id.sender_name_text)


    fun bind(message: GroupMessage) {
        text.text = message.messageText

        itemView.setOnLongClickListener {
            onLongClick(message)
        }
    }

    fun setSenderName(senderName: String) {
        senderNameText.text = senderName
    }

}

//Это мы при отправке сообщения
class GroupMeViewHolder(view: View,private val onLongClick: (GroupMessage) -> Boolean) : RecyclerView.ViewHolder(view) {

    private val text = itemView.findViewById<TextView>(R.id.text)
    private val senderNameText= itemView.findViewById<TextView>(R.id.sender_name_text)

    fun bind(message: GroupMessage) {
        text.text = message.messageText

        itemView.setOnLongClickListener {
            onLongClick(message)
        }
    }

    fun setSenderName(senderName: String) {
        senderNameText.text = senderName
    }

}