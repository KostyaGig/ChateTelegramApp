package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.extensions.replaceFragment
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.ui.viewmodels.LoginViewModel
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.coroutines.flow.collect

class LoginFragment : Fragment() {

    private lateinit var viewModel:LoginViewModel
    private val TAG = "LoginFragment"

    private lateinit var fieldEmail:EditText
    private lateinit var fieldPassword:EditText
    private lateinit var loginBtn:Button
    private lateinit var resetUserBtn: FloatingActionButton
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.login_layout_fragment,container,false)

        changeTitleToolbar("Login")
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        fieldEmail = root.findViewById(R.id.field_email)
        fieldPassword = root.findViewById(R.id.field_password)
        loginBtn = root.findViewById(R.id.login_btn)
        resetUserBtn = root.findViewById(R.id.reset_user_btn)
        progressBar = root.findViewById(R.id.progress_bar)

        loginBtn.setOnClickListener {
            viewModel.login(fieldEmail.text.toString(),fieldPassword.text.toString())
        }

        resetUserBtn.setOnClickListener {
            viewModel.resetUser()
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.existUser()) {
            replaceFragment(MainFragment())
        } else {
            observe()
        }
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "Loading state")
                        viewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Empty -> Log.d(TAG, "observe: Empty state")
                    is States.Failure -> {
                        Log.d(TAG, "state failure null")
                        if (state.message == "null") {
                            Toast.makeText(requireContext(), "NUll", Toast.LENGTH_SHORT).show()
                            viewModel.dismissLoadingProgressDialog()
                        } else {
                            showMessage(state.message,requireContext())
                            resetUserBtn.visibility = View.VISIBLE
                            viewModel.dismissLoadingProgressDialog()
                        }
                    }
                    is States.Success -> {
                        Log.d(TAG, "Success state")
                        replaceFragment(MainFragment())
                        viewModel.dismissLoadingProgressDialog()
                    }
                }
            }
        }
    }

}