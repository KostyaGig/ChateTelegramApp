package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.Group
import com.admin.project_kwork.models.GroupMessage
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.recyclerView.dataObservers.GroupDataObserver
import com.admin.project_kwork.ui.recyclerView.viewHolders.GroupFriendViewHolder
import com.admin.project_kwork.ui.recyclerView.viewHolders.GroupMeViewHolder
import com.admin.project_kwork.ui.viewmodels.GroupChatViewModel
import com.admin.project_kwork.utils.BundleConstans
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.ViewType
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.coroutines.flow.collect

class GroupChatFragment : Fragment(R.layout.group_chat_layout_fragment) {

    companion object {
        const val TAG = "GroupChatFragment"
    }

    private val groupChatViewModel by lazy {
        ViewModelProviders.of(this).get(GroupChatViewModel::class.java)
    }

    private lateinit var joinedInTheGroupBtn: Button
    private lateinit var sendMessageContainer: LinearLayout

    private lateinit var userMessageField: EditText
    private lateinit var sendMessageBtn: Button

    private lateinit var recAdapter: FirebaseRecyclerAdapter<GroupMessage, RecyclerView.ViewHolder>
    private lateinit var recycler: RecyclerView
    private lateinit var groupDataObserver: GroupDataObserver
    private val listenersMap = HashMap<DatabaseReference, ValueEventListener>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val clickedGroup: Group? = it.getSerializable(BundleConstans.USER) as Group ?: Group()
            groupChatViewModel.setClickedGroup(clickedGroup = clickedGroup)
            Log.d(TAG, "title -> ${groupChatViewModel.getClickedGroup()!!.title},id -> ${groupChatViewModel.getClickedGroup()!!.groupId}")
            changeTitleToolbar(groupChatViewModel.getClickedGroup()!!.title)
            groupChatViewModel.getAllUsersJoinedInTheCurrentGroupFromFirebase()
        }

        joinedInTheGroupBtn = view.findViewById(R.id.joined_in_the_group_btn)
        sendMessageContainer = view.findViewById(R.id.send_message_container)

        userMessageField = view.findViewById(R.id.field_message)
        sendMessageBtn = view.findViewById(R.id.send_message_btn)


        recycler = view.findViewById(R.id.group_chat_recycler_view)

        joinedInTheGroupBtn.setOnClickListener {
            groupChatViewModel.joinInTheGroup()
        }

        sendMessageBtn.setOnClickListener {
            sendMessage { message-> message.isNotEmpty() }
        }

        val query = FirebaseDatabase.getInstance().reference
            .child(FirebasePath.GROUPS_REF)
            .child(groupChatViewModel.getClickedGroup()!!.groupId)
            .child(FirebasePath.MESSAGES_REF) as Query

        //init firebase recycler options (show all groups)
        val options = FirebaseRecyclerOptions.Builder<GroupMessage>()
            .setLifecycleOwner(this)
            .setQuery(query,GroupMessage::class.java)
            .build()

        recAdapter = object: FirebaseRecyclerAdapter<GroupMessage,RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return if (viewType == ViewType.ME_IN_GROUP) {
                    GroupMeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.me_in_group_item,parent,false)) {message->
                        Log.d(TAG, "group message long cliked, message text-> ${message.messageText}")
                        true
                    }
                } else {
                    GroupFriendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friend_in_group_item,parent,false)) { message->
                        Log.d(TAG, "group message long cliked, message text-> ${message.messageText}")
                        true
                    }
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                model: GroupMessage
            ) {
                val currentGroupMessage = getItem(position)

                val senderUId = currentGroupMessage.senderId
                val currentUserReference = FirebaseDatabase
                    .getInstance()
                    .reference
                    .child(FirebasePath.USERS_REF)
                    .child(senderUId)

                if (holder is GroupMeViewHolder) {
                    holder.bind(currentGroupMessage)
                    val listener = object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val senderUser = snapshot.getValue(User::class.java)
                            Log.d("GroupChatFragment","sender user name -> ${senderUser?.userName}")
                            holder.setSenderName(senderUser!!.userName)
                        }
                    }

                    currentUserReference.addValueEventListener(listener)

                    listenersMap[currentUserReference] = listener
                } else if (holder is GroupFriendViewHolder) {
                    holder.bind(currentGroupMessage)
                    val listener = object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val senderUser = snapshot.getValue(User::class.java)
                            Log.d("GroupChatFragment","sender user name -> ${senderUser?.userName}")
                            holder.setSenderName(senderUser!!.userName)
                        }
                    }

                    currentUserReference.addValueEventListener(listener)

                    listenersMap[currentUserReference] = listener
                } else {
                    Log.d(TAG, "Holder not found")
                }
            }

            override fun getItemViewType(position: Int): Int {
                val currentMessage = getItem(position)
                Log.d(TAG, "SENDER ID -> ${currentMessage.senderId} ")
                return if (currentMessage.senderId == groupChatViewModel.currentUID()) {
                    ViewType.ME_IN_GROUP
                } else {
                    Log.d(TAG, "FriendInGroup")
                    ViewType.FRIEND_IN_GROUP
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                Log.d(TAG, "onDataChanged: Itemcount -> $itemCount")
            }

        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            groupChatViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "Loading state")
                        changeTitleToolbar("Loading...")
                        groupChatViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Empty -> {
                        Log.d(TAG, "Empty State")
                        changeTitleToolbar(groupChatViewModel.getClickedGroup()!!.title)
                        groupChatViewModel.dismissLoadingProgressDialog()
                    }
                    is States.Failure -> {
                        Log.d(TAG, "state failure")
                        groupChatViewModel.dismissLoadingProgressDialog()
                        showMessage(state.message, requireContext())
                    }
                    is States.Success -> {
                        Log.d(TAG, "state success")
                        groupChatViewModel.dismissLoadingProgressDialog()
                        //Мы вступили в группу
                        joinedInTheGroupBtn.visibility = View.GONE
                        sendMessageContainer.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            groupChatViewModel.isJoinedUser.collect {isJoin->
                if (isJoin) {
                    joinedInTheGroupBtn.visibility = View.GONE
                    sendMessageContainer.visibility = View.VISIBLE

                    //Если юзер вступил в группу,загружаем все сообщения из groupmessagesref
                    startAdapter()
                } else {
                    sendMessageContainer.visibility = View.GONE
                    joinedInTheGroupBtn.visibility = View.VISIBLE
                }

                Log.d(TAG, "User JOINDED -> $isJoin ")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAdapter()
        recAdapter.unregisterAdapterDataObserver(groupDataObserver)
        //unregister value event listener
        listenersMap.forEach {
            Log.d(TAG, "key-> ${it.key} value -> ${it.value}")
            it.key.removeEventListener(it.value)
        }
    }

    private fun sendMessage(sendMessage: (String) -> Boolean) {
        val message = userMessageField.text.toString().trim()
        if (sendMessage(message)) {
            userMessageField.setText("")
            groupChatViewModel.sendMessage(message)
        } else {
            showMessage("Enter message,please...",requireContext())
        }
    }

    private fun startAdapter() {
        recAdapter.startListening()
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.apply {
            adapter = recAdapter
            this.layoutManager = layoutManager
        }
        groupDataObserver = GroupDataObserver(recAdapter,layoutManager,recycler)
        recAdapter.registerAdapterDataObserver(groupDataObserver)
        
    }

    private fun stopAdapter() {
        recAdapter.stopListening()
    }

}