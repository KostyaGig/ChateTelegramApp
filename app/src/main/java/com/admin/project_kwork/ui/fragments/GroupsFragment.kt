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
import com.admin.project_kwork.ui.recyclerView.viewHolders.GroupsViewHolder
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

class GroupsFragment: Fragment(R.layout.list_groups_layout_fragment) {

    //Create FirebaseRecyclerView

    companion object {
        const val TAG = "GroupsFragment"
    }

    private val groupsViewModel by lazy {
        ViewModelProviders.of(this).get(GroupsViewModel::class.java)
    }

    private lateinit var recycler: RecyclerView

    private val query =
        FirebaseDatabase.getInstance().reference.child(FirebasePath.GROUPS_REF) as Query

    private val options = FirebaseRecyclerOptions.Builder<Group>()
        .setLifecycleOwner(this)
        .setQuery(query, Group::class.java)
        .build()

    private var newGroups = false

    private val recAdapter = object : FirebaseRecyclerAdapter<Group, GroupsViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
            return GroupsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.groups_layout_item, parent, false),
                { onClickedGroup ->
                    replaceWithData(GroupChatFragment(),addToBackStack = true,data = onClickedGroup)
                }, { longClickedGroup ->
                    Log.d(TAG, "LONG CLIKED group title -> ${longClickedGroup.title}")
                }
            )
        }

        override fun onBindViewHolder(
            holder: GroupsViewHolder,
            position: Int,
            currentGroup: Group
        ) {
            holder.bind(currentGroup,groupsViewModel.currentUID(),newGroups)
        }

        override fun onDataChanged() {
            super.onDataChanged()
            Log.d(TAG, "onDataChanged: ")
            groupsViewModel.changeCountGroups(itemCount)

            //Success downloading data to recyclerView
            groupsViewModel.setEmptyUIState()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("List groups")

        recycler = view.findViewById(R.id.list_groups_recycler_view)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        observe()

        recAdapter.let { adapter ->
            adapter.startListening()
        }

        recycler.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onPause() {
        super.onPause()
        recAdapter.let { adapter ->
            adapter.stopListening()
        }
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            groupsViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "Loading state")
                        changeTitleToolbar("Loading...")
                        groupsViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Empty -> {
                        Log.d(TAG, "Empty State")
                        groupsViewModel.dismissDialog()
                    }
                    is States.Failure -> {
                        Log.d(TAG, "state failure")
                        groupsViewModel.dismissDialog()
                        showMessage(state.message, requireContext())
                    }
                    is States.Success -> {
                        Log.d(TAG, "state success")
                        groupsViewModel.dismissDialog()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            //Subscribe on changed groups count
            groupsViewModel.groupsCount.collect { groupsCount->
                Log.d(TAG, "observe: groups count -> $groupsCount")
                if (groupsCount == 0) {
                    changeTitleToolbar("List groups")
                } else {
                    changeTitleToolbar("Count groups -> $groupsCount")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.groups_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sort_in_all_groups_item -> {
                newGroups = false
                recAdapter.updateOptions(options)
            }
            R.id.sort_in_new_groups_item -> {
                newGroups = true
                recAdapter.updateOptions(options)
            }
        }
        return true
    }

}