package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.ChatMessage
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.recyclerView.viewHolders.MeViewHolder
import com.admin.project_kwork.ui.recyclerView.viewHolders.FriendViewHolder
import com.admin.project_kwork.ui.viewmodels.UserChatViewModel
import com.admin.project_kwork.utils.BundleConstans
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.ViewType
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.coroutines.flow.collect

class ChatFragment : Fragment(R.layout.chat_layout_fragment) {

    private val TAG = "ChatFragment"
    private lateinit var chatViewModel: UserChatViewModel

    private lateinit var recAdapter: FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder>
    private lateinit var recycler: RecyclerView

    private lateinit var progressBar: ProgressBar
    private lateinit var sendMessageBtn: FloatingActionButton
    private lateinit var messageField: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel = ViewModelProviders.of(this).get(UserChatViewModel::class.java)

        recycler = view.findViewById(R.id.chat_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        sendMessageBtn = view.findViewById(R.id.send_message_btn)
        messageField = view.findViewById(R.id.field_message)

        sendMessageBtn.setOnClickListener {
            if (messageField.text.trim().toString().isNotEmpty()) {
                chatViewModel.sendMessage(messageField.text.toString())
                messageField.setText("")
            } else {
                sendMessageBtn.visibility = View.GONE
            }
        }

        messageField.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.trim().isNotEmpty()) {
                    if (sendMessageBtn.visibility != View.VISIBLE) {
                        //Play animation
                        showButtonAnimation()
                    }
                } else {
                    hideButtonAnimation()
                }
            }

        })

        arguments?.let {
            val clickedUser: User? =  it.getSerializable(BundleConstans.USER) as User? ?: User()
            chatViewModel.setClickedUser(clickedUser = clickedUser)
        }

        val query = FirebaseDatabase.getInstance().reference.child(FirebasePath.MESSAGES_REF).child(chatViewModel.getClickedUser()!!.uid) as Query

        //init firebase recycler options
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setLifecycleOwner(this)
            .setQuery(query,ChatMessage::class.java)
            .build()

         recAdapter = object : FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return if (viewType == ViewType.ME_IN_CHAT) {
                    MeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.me_in_chat_item,parent,false)){ onLongClickedMessage->
                        if (onLongClickedMessage.senderId == chatViewModel.currentUID()) {
                            chatViewModel.deleteMessage(onLongClickedMessage)
                        }
                        true
                    }
                } else {
                    FriendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friend_in_chat_item,parent,false)){ onLongClickedMessage->
                        if (onLongClickedMessage.senderId == chatViewModel.currentUID()) {
                            chatViewModel.deleteMessage(onLongClickedMessage)
                        }
                        true
                    }
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                message: ChatMessage
            ) {
                if (holder is MeViewHolder) {
                    holder.bind(message)
                } else if (holder is FriendViewHolder) {
                    holder.bind(message)
                }
            }

             override fun getItemViewType(position: Int): Int {
                 val currentMessage = getItem(position)
                 Log.d(GroupChatFragment.TAG, "SENDER ID -> ${currentMessage.senderId} ")
                 return if (currentMessage.senderId == chatViewModel.currentUID()) {
                     ViewType.ME_IN_CHAT
                 } else {
                     ViewType.FRIEND_IN_CHAT
                 }
             }

             override fun onDataChanged() {
                 super.onDataChanged()

                 //Success downloading data to recyclerView
                 chatViewModel.setEmptySate()
             }
        }
    }

    private fun showButtonAnimation() {
        sendMessageBtn.visibility = View.VISIBLE
        val animation = TranslateAnimation(50.0f,0.0f,0.0f,0.0f)
        animation.duration = 250
        animation.fillAfter = true
        sendMessageBtn.startAnimation(animation)
    }

    private fun hideButtonAnimation() {
        val animation = TranslateAnimation(0.0f,150.0f,0.0f,0.0f)
        animation.duration = 250
        animation.fillAfter = true
        sendMessageBtn.startAnimation(animation)
        sendMessageBtn.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        observe()

        recAdapter.let { adapter->
            adapter.startListening()
        }

        recycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun onPause() {
        super.onPause()
        recAdapter.let { adapter->
            adapter.stopListening()
        }
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            chatViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "Loading state")
                        changeTitleToolbar("Loading...")
                        chatViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Empty -> {
                        chatViewModel.dismissLoadingProgressDialog()
                        Log.d(TAG, "Empty State")
                        changeTitleToolbar(chatViewModel.getClickedUser()!!.userName)
                    }
                    is States.Failure -> {
                        Log.d(TAG, "state failure")
                        chatViewModel.dismissLoadingProgressDialog()
                        showMessage(state.message,requireContext())
                    }
                    is States.Success -> {
                        Log.d(TAG, "state success")
                        chatViewModel.dismissLoadingProgressDialog()
                    }
                }
            }
        }
    }

}