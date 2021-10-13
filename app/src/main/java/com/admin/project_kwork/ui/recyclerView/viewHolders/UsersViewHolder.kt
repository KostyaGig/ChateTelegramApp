package com.admin.project_kwork.ui.recyclerView.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.User
import com.bumptech.glide.Glide

class UsersViewHolder(view: View, private val onClick: (User) -> Unit,private val onLongClick: (User) -> Unit) : RecyclerView.ViewHolder(view) {

    private val userName = itemView.findViewById<TextView>(R.id.user_name_text)
    private val userEmail = itemView.findViewById<TextView>(R.id.user_email_text)
    private val profileImage = itemView.findViewById<ImageView>(R.id.user_profile_image)

    fun bind(currentUser: User) {


        userEmail.text = currentUser.email

        if (currentUser.userName != "") {
            userName.text = currentUser.userName
        } else {
            userName.text = "Имя не найдено"
        }

        if (currentUser.imageUrl != "") {
            Glide
                .with(itemView)
                .load(currentUser.imageUrl)
                .placeholder(R.drawable.ic_person)
                .into(profileImage)
        }

        itemView.setOnClickListener {
            onClick(currentUser)
        }

        itemView.setOnLongClickListener {
            onLongClick(currentUser)
            true
        }
    }

}
