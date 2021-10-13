package com.admin.project_kwork.ui.recyclerView.viewHolders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.Group
import com.bumptech.glide.Glide

class GroupsViewHolder(view: View, private val onClick: (Group) -> Unit, private val onLongClick: (Group) -> Unit) : RecyclerView.ViewHolder(view) {

    private val groupIdTextView = itemView.findViewById<TextView>(R.id.group_id_text)
    private val titleGroupTextView = itemView.findViewById<TextView>(R.id.title_group_text)

    fun bind(currentGroup: Group,uId: String?,newGroups: Boolean) {

        //TODO EDIT ADMINID НА GROUPID
        groupIdTextView.text = currentGroup.adminId
        titleGroupTextView.text = currentGroup.title

        Log.d("GroupsFragment", "SORT IN NEW GROUPS -> $newGroups ")

        //Если хотим отображать только свои группы,еслти хотим отображать только групппы в которые не вступили,надо проверять uid участников группы с uid текущего юзера
        if (newGroups) {
            if (uId != currentGroup.adminId) {
                itemView.visibility = View.GONE
                Log.d("GroupsFragment","uid == admin id hide item")
            }
        } else {
            itemView.visibility = View.VISIBLE
        }

        itemView.setOnClickListener {
            onClick(currentGroup)
        }

        itemView.setOnLongClickListener {
            onLongClick(currentGroup)
            true
        }
    }

}
