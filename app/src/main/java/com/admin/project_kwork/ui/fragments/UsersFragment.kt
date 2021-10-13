package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.recyclerView.viewHolders.UsersViewHolder
import com.admin.project_kwork.ui.viewmodels.UsersViewModel
import com.admin.project_kwork.utils.State
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.extensions.replaceWithData
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.admin.project_kwork.utils.extensions.showMessage
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.coroutines.flow.collect

class UsersFragment() : Fragment(R.layout.users_layout_fragment) {

    private val TAG = "UsersFragment"
    private lateinit var usersViewModel: UsersViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var recycler: RecyclerView
    private val query = FirebaseDatabase.getInstance().reference.child(FirebasePath.USERS_REF) as Query


    private val options = FirebaseRecyclerOptions.Builder<User>()
        .setLifecycleOwner(this)
        .setQuery(query,User::class.java)
        .build()

    private val recAdapter = object : FirebaseRecyclerAdapter<User,UsersViewHolder>(options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
            val root = LayoutInflater.from(parent.context).inflate(R.layout.users_item,parent,false)
            return UsersViewHolder(root,
                {clickedUser->
                if (clickedUser.uid == usersViewModel.currentUID()) {
                    showMessage("Это вы",requireContext())
                } else {
                    replaceWithData(ChatFragment(),addToBackStack = true,data = clickedUser)
                }
                },{currentUser->
                    usersViewModel.createProfileUserDialog(requireContext(),currentUser = currentUser)
                    usersViewModel.showProfileUserDialog()
                })
        }

        override fun onBindViewHolder(holder: UsersViewHolder, position: Int, user: User) {
            holder.bind(user)
        }

        override fun onDataChanged() {
            super.onDataChanged()
            if (itemCount == 0) {
                showMessage("Пустой список",requireContext())
            }

            recycler.smoothScrollToPosition(itemCount + 1)
            Log.d(TAG,"onDataChanged ,item count -> ${itemCount}")

            //Success downloading data to recyclerView
            usersViewModel.setEmptyUISate()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Users")

        progressBar = view.findViewById(R.id.progress_bar)
        recycler = view.findViewById(R.id.users_recycler_view)

        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)

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
            usersViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "Loading state")
                        changeTitleToolbar("Loading...")
                        usersViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Empty -> {
                        Log.d(TAG, "Empty State")
                        changeTitleToolbar("Users")
                        usersViewModel.dismissDialog()
                    }
                    is States.Failure -> {
                        Log.d(TAG, "state failure")
                        usersViewModel.dismissDialog()
                        showMessage(state.message,requireContext())
                    }
                    is States.Success -> {
                        Log.d(TAG, "state success")
                        usersViewModel.dismissDialog()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            usersViewModel.sendMessageState.collect { state ->
                when (state) {
                    is State.Empty -> Log.d(TAG, "State state empty ")
                    is State.SendMessage -> {
                        Log.d(TAG, "State send message user -> ${state.currentUser.email}")
                        replaceWithData(ChatFragment(),addToBackStack = true,data = state.currentUser)
                        //Убери это и при нажатии в диалоге сенд мэссэдж и при возврате назад по бэкстэку увидишь что будет
                        usersViewModel.setEmptySendMessageState()
                    }
                }
            }
        }
    }

}

