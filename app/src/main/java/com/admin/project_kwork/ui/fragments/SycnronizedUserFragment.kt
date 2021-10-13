package com.admin.project_kwork.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.extensions.popBackStack
import com.admin.project_kwork.utils.extensions.showMessage
import com.admin.project_kwork.ui.viewmodels.SyncronizedUserViewModel
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import kotlinx.coroutines.flow.collect

class SycnronizedUserFragment : Fragment(R.layout.syncronized_user_layout_fragment) {

    private val TAG = "SycnronizedUserFragment"
    private lateinit var syncronizedUserViewModel: SyncronizedUserViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var synchronize:TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Synchronized data")
        syncronizedUserViewModel = ViewModelProviders.of(this).get(SyncronizedUserViewModel::class.java)

        progressBar = view.findViewById(R.id.progress_bar)
        synchronize = view.findViewById(R.id.synchronize)
        synchronize.setOnClickListener {
            //TODO show custom dialog
            syncronizedUserViewModel.syncronizedData()
        }

    }

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            syncronizedUserViewModel.uiState.collect {state->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "SycnronizedUserFragment state -> Loading")
                        progressBar.visibility = View.VISIBLE
                    }
                    is States.Empty -> {
                        Log.d(TAG, "SycnronizedUserFragment state -> Empty")
                        progressBar.visibility = View.GONE
                    }
                    is States.Success -> {
                        //TODO show custom dialog
                        progressBar.visibility = View.GONE
                        showMessage("Actual data don't was found",requireContext())
                        popBackStack()
                    }
                    is States.Failure -> {
                        if (state.message == "Actual") {
//                            TODO Show custom dialog
                            progressBar.visibility = View.GONE
                            showMessage("Actual data found,please updates data",requireContext())
                            popBackStack()
                        } else {
                            progressBar.visibility = View.GONE
                            Log.d(TAG, "SycnronizedUserFragment state -> Failure,message -. ${state.message}}")
                        }
                    }
                }
            }
        }
    }
}