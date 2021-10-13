package com.admin.project_kwork.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.admin.project_kwork.R
import com.admin.project_kwork.utils.*
import com.admin.project_kwork.ui.viewmodels.EditUserProfileImageViewModel
import com.admin.project_kwork.utils.extensions.popBackStack
import com.admin.project_kwork.utils.extensions.setImage
import com.admin.project_kwork.utils.extensions.changeTitleToolbar
import com.admin.project_kwork.utils.extensions.showMessage
import kotlinx.coroutines.flow.collect

class EditImageProfileFragment : BaseFragment(R.layout.edit_profile_image_layout_fragment) {

    private val TAG = "EditImageFragment"
    private lateinit var editImageViewModel: EditUserProfileImageViewModel

    private lateinit var progressBar: ProgressBar
    private lateinit var userProfileImage: ImageView
    private lateinit var updateUserProfileImageBtn: Button

    private var imageUri: Uri? = null
    val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {imageUri: Uri? ->
        Log.d(TAG, "getImage branch, URI -> $imageUri ")

        userProfileImage.setImageURI(imageUri)
        this.imageUri = imageUri
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitleToolbar("Edit image")
        editImageViewModel = ViewModelProviders.of(this).get(EditUserProfileImageViewModel::class.java)

        progressBar = view.findViewById(R.id.progress_bar)
        updateUserProfileImageBtn = view.findViewById(R.id.update_user_profile_image_btn)
        userProfileImage = view.findViewById(R.id.user_profile_image)

        userProfileImage.setOnClickListener {
            getImage.launch("image/*")
        }

        updateUserProfileImageBtn.setOnClickListener {
            checkImage(imageUri = imageUri) {uri ->
                uri != null
            }
        }

        editImageViewModel.getLocalUser()
    }

    override fun onStart() {
        super.onStart()
            observe()
        }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            //Local user states
            editImageViewModel.userLocalState.collect { state ->
                when (state) {
                    is LocalUserStates.Loading -> {
                        if (state.isLoading) {
                            Log.d(TAG, "Localuser State -> Loading")
                            editImageViewModel.showLoadingProgressDialog(requireContext())
                        } else {
                            Log.d(TAG, "Localuser State -> Not loading")
                            editImageViewModel.dismissLoadingProgressDialog()
                        }
                    }
                    is LocalUserStates.EmptyProfileImage -> {
                        Log.d(TAG, "Localuser State -> Empty image profile")

                        userProfileImage.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_person))
                        editImageViewModel.dismissLoadingProgressDialog()
                    }
                    is LocalUserStates.Success -> {
                        Log.d(TAG, "Localuser State success loading user from data store")
                        setImage("https://images.app.goo.gl/yx5fLrFmiyuKytGFA",userProfileImage)
                        editImageViewModel.dismissLoadingProgressDialog()
                    }
                    else -> Log.d(TAG, "Localuser State -> else ")
                }
            }
        }

        //ui state (edit user profile image)
        lifecycleScope.launchWhenStarted {
            editImageViewModel.uiState.collect { state ->
                when (state) {
                    is States.Loading -> {
                        Log.d(TAG, "UI state -> loading")
                        editImageViewModel.showLoadingProgressDialog(requireContext())
                    }
                    is States.Failure -> {
                        Log.d(TAG, "UI state -> failure upload user image, message -> ${state.message}")
                        editImageViewModel.dismissLoadingProgressDialog()
                    }
                    is States.Success -> {
                        editImageViewModel.dismissLoadingProgressDialog()
                        showMessage("Image was success updated",requireContext())
                        popBackStack()
                    }
                    else -> Log.d(TAG, "UI state -> else")
                }
            }

        }
    }

    //Заюзамм лямбду для пррактики
    private fun checkImage(imageUri:Uri?,checkImage: (Uri?) -> (Boolean))  {
        if (checkImage(imageUri)) {
            editImageViewModel.updateProfileImage(imageUri = imageUri)
        } else {
            //TODO Если будут утечки памяти,то вместо requireActivity ЮЗАЙ requireContext()
            showMessage("Вы не выбрали фотографию",requireContext())
            popBackStack()
        }
    }

}