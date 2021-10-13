package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import coil.load
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.LocalUserStates
import com.admin.project_kwork.ui.viewmodels.BioViewModel
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import kotlinx.coroutines.flow.collect

class BioFragment : Fragment(R.layout.bio_layout_fragment){

    private val TAG = "BioFragment"
    
    private lateinit var bioViewModel:BioViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var userProfileImage: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPassword: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Bio")
        bioViewModel = ViewModelProviders.of(this).get(BioViewModel::class.java)

        // init view
        progressBar = view.findViewById(R.id.progress_bar)
        userProfileImage = view.findViewById(R.id.user_profile_image)
        userName = view.findViewById(R.id.user_name_tv)
        userEmail = view.findViewById(R.id.user_email_tv)
        userPassword = view.findViewById(R.id.user_password_tv)

        bioViewModel.getLocalUser()
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            bioViewModel.localUserState.collect { state ->
                when (state) {
                    is LocalUserStates.Loading -> {
                        if (state.isLoading) {
                            //Show progress loading
                            bioViewModel.showLoadingProgressDialog(requireContext())
                        } else {
                            Log.d(TAG, "State loading -> false")
                            bioViewModel.dismissLoadingProgressDialog()
                        }
                    }
                    is LocalUserStates.Success -> {
                        Log.d(TAG, "observe: Success")
                        userName.text = state.localUser.userName
                        userEmail.text = state.localUser.email
                        userPassword.text = state.localUser.password

                        //TODO Решить вопросв с загрузкой фото
                        userProfileImage.load("https://firebasestorage.googleapis.com/v0/b/fir-testapp-b6eb1.appspot.com/o/profileImages?alt=media&token=5ddc74bc-040c-4427-92a0-889b426232ce")
                        bioViewModel.dismissLoadingProgressDialog()
                    }
                    is LocalUserStates.EmptyName -> {
                        Log.d(TAG, "observe: Empty name")
                        userName.text = "Здесь должно быть ваше имя"
                        userEmail.text = state.localUser.email
                        userPassword.text = state.localUser.password
                        bioViewModel.dismissLoadingProgressDialog()
                    }
                    is LocalUserStates.EmptyProfileImage -> {
                        userName.text = state.localUser.userName
                        userEmail.text = state.localUser.email
                        userPassword.text = state.localUser.password
                        Log.d(TAG, "observe: EMptyO,age")
                        bioViewModel.dismissLoadingProgressDialog()
                    }
                    is LocalUserStates.EmptyBio -> {
                        userName.text = "Здесь должно быть ваше имя"
                        userEmail.text = state.localUser.email
                        userPassword.text = state.localUser.password
                        Log.d(TAG, "observe:EMptyBio ")
                        bioViewModel.dismissLoadingProgressDialog()
                    }
                }
            }
        }
    }
}