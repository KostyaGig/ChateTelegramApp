package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.admin.project_kwork.R
import com.admin.project_kwork.ui.viewmodels.CreateGroupViewModel
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.popBackStack
import com.admin.project_kwork.utils.extensions.showMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect

class CreateGroupFragment : Fragment(R.layout.create_group_layout_fragment) {

    companion object {
        const val TAG = "CreateGroup"

        const val EMPTY_TITLE_GROUP = 1
        const val SHORT_TITLE_GROUP = 2
        const val SUCCESS_TITLE = 3
    }

    private val createGroupViewModel by lazy {
        ViewModelProviders.of(this).get(CreateGroupViewModel::class.java)
    }

    private lateinit var titleGroupField: EditText
    private lateinit var createGroupBtn: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Create group")

        createGroupBtn = view.findViewById(R.id.create_group_btn)
        titleGroupField = view.findViewById(R.id.title_group_field)

        createGroupBtn.setOnClickListener {
            checkTitleGroup { title->
                if (title.isEmpty()) {
                    EMPTY_TITLE_GROUP
                } else if (title.length < 4){
                    SHORT_TITLE_GROUP
                } else {
                    SUCCESS_TITLE
                }
            }
        }
    }

    fun checkTitleGroup(checkTitle:(title:String) -> (Int)) {
        if (checkTitle(titleGroupField.text.toString().trim()) == EMPTY_TITLE_GROUP) {
            createGroupViewModel.createGroup(titleGroupField.text.toString().trim())
        } else if (checkTitle(titleGroupField.text.toString().trim()) == SHORT_TITLE_GROUP){
            showMessage("Название группы составляет -> не менее 4 символов",requireContext())
        } else {
            createGroupViewModel.createGroup(titleGroupField.text.toString().trim())
        }
    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            createGroupViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "UI state -> loading")
                        changeTitleToolbar("Creating...")
                        createGroupViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Failure -> {
                        Log.d(
                            TAG,
                            "UI state -> failure upload user name, message -> ${state.message}"
                        )
                        createGroupViewModel.dismissLoadingProgressDialog()
                    }
                    is States.Success -> {
                        createGroupViewModel.dismissLoadingProgressDialog()
                        showMessage("Success created group!", requireContext())
                        popBackStack()
                    }
                    else -> Log.d(TAG, "UI state -> else")
                }
            }
        }
    }
}