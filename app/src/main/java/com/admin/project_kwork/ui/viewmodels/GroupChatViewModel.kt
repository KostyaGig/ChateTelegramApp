package com.admin.project_kwork.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.admin.project_kwork.models.Group
import com.admin.project_kwork.models.GroupMessage
import com.admin.project_kwork.ui.customviews.dialogs.DialogConfiguration
import com.admin.project_kwork.ui.fragments.GroupChatFragment
import com.admin.project_kwork.utils.States
import com.admin.project_kwork.utils.firebase.FirebasePath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupChatViewModel(application: Application) : AndroidViewModel(application) {

    private val dialogConfiguration = DialogConfiguration()

    private val _uiState = MutableStateFlow<States>(States.Loading)
    val uiState:StateFlow<States> = _uiState

    private val _isJoinedUser = MutableStateFlow<Boolean>(false)
    val isJoinedUser:StateFlow<Boolean> get() = _isJoinedUser

    private var clickedGroup: Group? = Group()



//    fun deleteMessage(message: ChatMessage) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val messageReference = FirebaseDatabase
//                .getInstance()
//                .reference
//                .child(FirebasePath.USER_MESSAGES_REF)
//
//            val currentMessageReference = messageReference
//                .child(message.receiverId)
//                .child(message.messageId)
//
//            currentMessageReference.removeValue()
//        }
//    }

    fun currentUID () = FirebaseAuth.getInstance().currentUser?.uid

    fun setClickedGroup(clickedGroup: Group?) {
        this.clickedGroup = clickedGroup
    }

    fun getClickedGroup(): Group? {
        return clickedGroup
    }

    fun setEmptySate() {
        _uiState.value = States.Empty
    }

    fun showLoadingProgressDialog(context: Context) {
        dialogConfiguration.createLoadingProgressDialog(context)
        dialogConfiguration.showDialog()
    }

    fun dismissLoadingProgressDialog() {
        dialogConfiguration.hideDialog()
    }

    //Будем проверять состоит ли юзер в группе или нет
    private fun checkIsJoinedUser(ids: List<String>) {
        viewModelScope.launch {
            //Проверяем вступил ли текущтий юзер в группу или нет
            ids.forEach { id->
                if (currentUID() == id) {
                    _isJoinedUser.value = true
                }
            }
            _uiState.value = States.Empty
        }
    }

    //Получаем id юзеров вступивших в группу,после возвращаем из список
     fun getAllUsersJoinedInTheCurrentGroupFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentGroupReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.GROUPS_REF)
                .child(clickedGroup!!.groupId)

            val joinedUsersReference = currentGroupReference
                .child(FirebasePath.JOINED_USERS)

            //Временная переменная,хранящая id вступивших в данную группу юзеров,ернем ее в async
            val idsJoinedUsers = mutableListOf<String>()

            //Делаем запрос на поулчения всех uid вступивших в данную группу
            joinedUsersReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = States.Failure(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (s in snapshot.children) {
                        Log.d(GroupChatFragment.TAG, "uid - > ${s.value}")
                        val uId = s.value
                        //Добавляем все id юзеров вступивших в группу
                        idsJoinedUsers.add(uId as String)
                    }
                    checkIsJoinedUser(idsJoinedUsers)
                }
            })
        }
    }

    //Вступить в группу
    fun joinInTheGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = States.Loading
            val currentGroupReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.GROUPS_REF)
                .child(clickedGroup!!.groupId)

            val joinedUsersReference = currentGroupReference
                .child(FirebasePath.JOINED_USERS)

            val uId = currentUID()
            joinedUsersReference.push().setValue(uId).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    _uiState.value = States.Success
                } else {
                    _uiState.value = States.Failure(task.exception?.message.toString())
                }
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO ){
            //get current group reference
            val currentGroupReference = FirebaseDatabase
                .getInstance()
                .reference
                .child(FirebasePath.GROUPS_REF)
                .child(clickedGroup!!.groupId)

            //get messages reference
            val groupMessagesReference = currentGroupReference
                .child(FirebasePath.MESSAGES_REF)

            //generate message id
            val messageId = groupMessagesReference.push().key

            val groupId = clickedGroup!!.groupId
            val senderId = currentUID()
            val message = GroupMessage(groupId = groupId,senderId = senderId!!,messageText = text)
            groupMessagesReference
                .child(messageId!!)
                .setValue(message)
        }
    }

}