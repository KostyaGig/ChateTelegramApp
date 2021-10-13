package com.admin.project_kwork.ui.recyclerView.dataObservers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.models.GroupMessage
import com.firebase.ui.database.FirebaseRecyclerAdapter
import java.security.PrivateKey

class GroupDataObserver(
    private val adapter: FirebaseRecyclerAdapter<GroupMessage, RecyclerView.ViewHolder>,
    private val layoutManager: LinearLayoutManager,
    private val recyclerView: RecyclerView) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val friendlyMessageCount: Int = adapter.getItemCount()
        val lastVisiblePosition: Int = layoutManager.findLastCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        if (lastVisiblePosition == -1 ||
            positionStart >= friendlyMessageCount - 1 &&
            lastVisiblePosition == positionStart - 1
        ) {
            recyclerView.scrollToPosition(positionStart)
        }
    }
}