package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.*
import com.admin.project_kwork.ui.viewmodels.EditUserNameViewModel
import com.admin.project_kwork.utils.extensions.popBackStack
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.showMessage
import kotlinx.coroutines.flow.collect

class EditNameFragment : BaseFragment(R.layout.edit_name_layout_fragment) {

    //TODO Во все проекты добавить ViewBinding
    private val TAG = "EditNameFragment"
    private lateinit var progressBar: ProgressBar
    private lateinit var userNameField: EditText

    private lateinit var editUserNameViewModel: EditUserNameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Edit name")
        editUserNameViewModel = ViewModelProviders.of(this).get(EditUserNameViewModel::class.java)
        progressBar = view.findViewById(R.id.progress_bar)
        userNameField = view.findViewById(R.id.user_name_field)

        view.findViewById<Button>(R.id.update_user_name_btn).setOnClickListener {
            checkName(userNameField.text.toString()) { userName ->
                userName.isNotEmpty()
            }
        }

        //Get user from datatstore
        editUserNameViewModel.getLocalUser()
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            //Local user states
            editUserNameViewModel.userLocalState.collect { state ->
                when (state) {
                    is LocalUserStates.Loading -> {
                        if (state.isLoading) {
                            Log.d(TAG, "Localuser State -> Loading")
                            editUserNameViewModel.showLoadingProgressDialog(requireContext())
                        } else {
                            Log.d(TAG, "Localuser State -> Not loading")
                            editUserNameViewModel.dismissLoadingProgressDialog()
                        }
                    }
                    is LocalUserStates.EmptyName -> {
                        Log.d(TAG, "Localuser State -> EMpty name")
                        editUserNameViewModel.dismissLoadingProgressDialog()
                        userNameField.setText("Здесь должно быть выше имя")
                    }
                    is LocalUserStates.Success -> {
                        Log.d(TAG, "Localuser State success loading user from data store")
                        editUserNameViewModel.dismissLoadingProgressDialog()
                        userNameField.setText(state.localUser.userName)
                    }
                    else -> {
                        Log.d(TAG, "Localuser State -> else")
                    }
                }
            }
        }

        //ui state (edit user name)
        lifecycleScope.launchWhenStarted {
            editUserNameViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "UI state -> loading")
                        editUserNameViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Failure -> {
                        Log.d(TAG, "UI state -> failure upload user name, message -> ${state.message}")
                        editUserNameViewModel.dismissLoadingProgressDialog()
                    }
                    is States.Success -> {
                        editUserNameViewModel.dismissLoadingProgressDialog()
                        showMessage("Success updated user name",requireContext())
                        popBackStack()
                    }
                    else -> Log.d(TAG, "UI state -> else")
                }
            }
        }
    }

    //ПИшу тданный метод только для того чтобы заюзать лямбду,практикуюсь_

    private fun checkName(name: String, checkName: (String) -> (Boolean)) {
        if (checkName(name)) {
            editUserNameViewModel.editUserName(name)
        } else {
            showMessage("Введи userName", requireActivity())
        }
    }
}
