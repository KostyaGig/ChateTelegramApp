package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.LocalUserStates
import com.admin.project_kwork.utils.extensions.replaceFragment
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.ui.viewmodels.MainViewModel
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect

class MainFragment : Fragment(R.layout.main_layout_fragment) {

    private val TAG = "ChatsFragment"
    private lateinit var mainViewModel: MainViewModel
    private lateinit var synchronizeUserBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Main")
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        synchronizeUserBtn = view.findViewById(R.id.synchronize_user_btn)

        view.findViewById<FloatingActionButton>(R.id.change_image_btn)?.setOnClickListener {
            replaceFragment(EditImageProfileFragment(),addToBackStack = true)
        }

        view.findViewById<FloatingActionButton>(R.id.change_name_btn)?.setOnClickListener {
            replaceFragment(EditNameFragment(),addToBackStack = true)
        }

        //save user to datastore
        view.findViewById<FloatingActionButton>(R.id.save_user_btn).setOnClickListener {
            mainViewModel.showSaveUserDialog(requireContext())
        }

        view.findViewById<FloatingActionButton>(R.id.users_btn).setOnClickListener {
            replaceFragment(UsersFragment(),addToBackStack = true)
        }

        view.findViewById<FloatingActionButton>(R.id.create_group_btn).setOnClickListener {
            replaceFragment(CreateGroupFragment(),addToBackStack = true)
        }

        view.findViewById<FloatingActionButton>(R.id.create_group_btn).setOnLongClickListener {
            replaceFragment(GroupsFragment(),addToBackStack = true)
            true
        }

        view.findViewById<FloatingActionButton>(R.id.music_btn).setOnClickListener {
            replaceFragment(MusicFragment())
        }

        synchronizeUserBtn.setOnClickListener {
            replaceFragment(SycnronizedUserFragment(),addToBackStack = true)
        }


        //get local User from data store if exist
        mainViewModel.getLocalUser()

        //TODO Все думаю как нам сохранить id групп в которые уступает юзер,предлагаю создать
        // отдельный датастор,где будет храниться данная инфа,конечно же инфу о вступленных группах следует пушить и на
        // ,создать отдеьную ветку joinde group,где хранить id встпуленных групп
        
        //TODO ОСТАЕТСЯ РЕШИТЬ ВОПРОС С КАРТИНКАМИ И ПОТОМ ЧАТ ГРУППА
        //TODO СДЕЛАТЬ ПРОВЕРКУ НА ИНТЕРНЕТ ЧЕРЕЗ ИНТЕРФЕЙС также бахнуть service для проигрывания фоновой музыки,можно релизовать интересненький motionLayout
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.localUserState.collect { state ->
                when (state) {
                    is LocalUserStates.Loading -> {
                        if (state.isLoading) {
                            mainViewModel.showLoadingProgressDialog(requireContext())
                        } else {
                            mainViewModel.dismissLoadingProgressDialog()
                        }
                    }
                    is LocalUserStates.Success -> {
                        Log.d(TAG, "Success user Name -> ${state.localUser.userName}")
                    }
                    is LocalUserStates.EmptyName -> Log.d(TAG, "State empty name ")
                    is LocalUserStates.EmptyProfileImage -> Log.d(
                        TAG,
                        "State empty profile imaage "
                    )
                    is LocalUserStates.EmptyBio -> showMessage(
                        "Name and image not save local",
                        requireContext()
                    )
                    is LocalUserStates.EmptyDataStore -> {
                        Log.d(TAG, "Empty data store")
                        synchronizeUserBtn.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.bio -> {
                replaceFragment(BioFragment(),addToBackStack = true)
            }
            R.id.exit -> {
                mainViewModel.existFromAccount()
                replaceFragment(LoginFragment())
            }
        }
        return true
    }

}