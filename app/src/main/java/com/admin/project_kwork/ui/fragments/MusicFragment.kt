package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.Group
import com.admin.project_kwork.models.Music
import com.admin.project_kwork.models.User
import com.admin.project_kwork.ui.recyclerView.viewHolders.GroupsViewHolder
import com.admin.project_kwork.ui.recyclerView.viewHolders.MusicViewHolder
import com.admin.project_kwork.ui.recyclerView.viewHolders.UsersViewHolder
import com.admin.project_kwork.ui.viewmodels.GroupsViewModel
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.replaceFragment
import com.admin.project_kwork.utils.extensions.replaceWithData
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kotlinx.coroutines.flow.collect

class MusicFragment: Fragment(R.layout.music_layout_fragment) {

    companion object {
        const val TAG = "MusicFragment"
    }

    private lateinit var recycler: RecyclerView
    private val query = FirebaseDatabase.getInstance().reference.child(FirebasePath.MUSIC_REF) as Query


    private val options = FirebaseRecyclerOptions.Builder<Music>()
        .setLifecycleOwner(this)
        .setQuery(query, Music::class.java)
        .build()

    private val recAdapter = object : FirebaseRecyclerAdapter<Music, MusicViewHolder>(options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
            val root = LayoutInflater.from(parent.context).inflate(R.layout.users_item,parent,false)
            return MusicViewHolder(root,
                {clickedMusic ->
                    showMessage("Music which have title -> ${clickedMusic.titleMusic} was clicked!",requireContext())
                },{currentMusic ->
                    showMessage("Music which have title -> ${currentMusic.titleMusic} was ONLONG clicked!",requireContext())
                })
        }

        override fun onBindViewHolder(holder: MusicViewHolder, position: Int, music: Music) {
            holder.bind(music)
        }

        override fun onDataChanged() {
            super.onDataChanged()
            if (itemCount == 0) {
                showMessage("Empty list",requireContext())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Musics")
    }

    override fun onStart() {
        super.onStart()
//        observe()

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

//
//    private fun observe() {
//        lifecycleScope.launchWhenStarted {
//            groupsViewModel.uiState.collect { state ->
//                when (state) {
//                    is States.Loading -> {
//                        Log.d(TAG, "Loading state")
//                        changeTitleToolbar("Loading...")
//                        groupsViewModel.showLoadingProgressDialog(requireContext())
//                    }
//                    is States.Empty -> {
//                        Log.d(TAG, "Empty State")
//                        groupsViewModel.dismissDialog()
//                    }
//                    is States.Failure -> {
//                        Log.d(TAG, "state failure")
//                        groupsViewModel.dismissDialog()
//                        showMessage(state.message, requireContext())
//                    }
//                    is States.Success -> {
//                        Log.d(TAG, "state success")
//                        groupsViewModel.dismissDialog()
//                    }
//                }
//            }
//        }
//
//        lifecycleScope.launchWhenStarted {
//            //Subscribe on changed groups count
//            groupsViewModel.groupsCount.collect { groupsCount->
//                Log.d(TAG, "observe: groups count -> $groupsCount")
//                if (groupsCount == 0) {
//                    changeTitleToolbar("List groups")
//                } else {
//                    changeTitleToolbar("Count groups -> $groupsCount")
//                }
//            }
//        }
//    }


}