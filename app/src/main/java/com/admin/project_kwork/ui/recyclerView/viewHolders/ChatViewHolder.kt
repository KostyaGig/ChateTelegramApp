package com.admin.project_kwork.ui.recyclerView.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.ChatMessage

class FriendViewHolder(view: View, private val onLongClick:(ChatMessage) -> Boolean) : RecyclerView.ViewHolder(view) {

    private val text = itemView.findViewById<TextView>(R.id.text)

    fun bind(message: ChatMessage) {
        text.text = message.messageText

        itemView.setOnLongClickListener {
            onLongClick(message)
        }
    }

}

//Это мы при отправке сообщения
class MeViewHolder(view: View,private val onLongClick: (ChatMessage) -> Boolean) : RecyclerView.ViewHolder(view) {

    private val text = itemView.findViewById<TextView>(R.id.text)

    fun bind(message: ChatMessage) {
        text.text = message.messageText

        itemView.setOnLongClickListener {
            onLongClick(message)
        }

    }

}