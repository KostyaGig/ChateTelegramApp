package com.admin.project_kwork.ui.customviews.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.User
import com.bumptech.glide.Glide
import com.google.api.Distribution
import kotlinx.android.synthetic.main.users_item.view.*

class ProfileUserDialog(context: Context,val chatBtnClick: (Dialog) -> Unit) : Dialog(context), View.OnClickListener {

    private lateinit var userProfileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView

    //Action button
    private lateinit var chatBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_user_dialog_layout)
        Log.d("PrDialog","oncreate")
        userProfileImage = findViewById(R.id.user_profile_image)
        userName = findViewById(R.id.user_name_text)
        userEmail = findViewById(R.id.user_email_text)

        chatBtn = findViewById(R.id.send_message_btn)
        chatBtn.setOnClickListener(this)
    }

    fun setUserProfile(currentUser: User?) {
        currentUser?.let {user->
//            Glide
//                .with(context)
//                .load(currentUser.imageUrl)
//                .placeholder(R.drawable.ic_person)
//                .into(userProfileImage)

            userName.text = currentUser.userName
            userEmail.text = currentUser.email
        }
    }

    override fun onClick(v: View?) {
        chatBtnClick(this)
    }

}