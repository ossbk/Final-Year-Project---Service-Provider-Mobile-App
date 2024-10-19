package com.services.provider.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserStatus
import com.services.provider.domain.repository.AuthRepository
import com.services.provider.domain.repository.MessageRepository
import com.services.provider.domain.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private val _usersResponse = MutableStateFlow<MyResponse<List<User>>?>(null)
    val usersResponse = _usersResponse.asStateFlow()


    fun getActiveUsers() {
        viewModelScope.launch {
            _usersResponse.value = repository.getActiveUsers()
        }
    }

    fun getPendingUsers() {
        viewModelScope.launch {
            _usersResponse.value = repository.getPendingUsers()
        }
    }

    fun approveUser(user: User,remove:Boolean=false) {
        viewModelScope.launch {
             repository.approveUser(user)
        }
        if (remove) {
            _usersResponse.value?.let {
                if (it is MyResponse.Success) {
                    val list = it.data.toMutableList()
                    list.remove(user)
                    _usersResponse.value = MyResponse.Success(list)
                }
            }
        }
    }

    fun rejectUser(user: User) {
         viewModelScope.launch {
             repository.rejectUser(user)
        }
        _usersResponse.value?.let {
            if (it is MyResponse.Success) {
                val list = it.data.toMutableList()
                list.remove(user)
                _usersResponse.value = MyResponse.Success(list)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
             repository.deleteUser(user)
        }
        _usersResponse.value?.let {
            if (it is MyResponse.Success) {
                val list = it.data.toMutableList()
                list.remove(user)
                _usersResponse.value = MyResponse.Success(list)
            }
        }
    }

    fun disableUser(user: User) {
        viewModelScope.launch {
             repository.disableUser(user)
        }
    }

    suspend fun getCurrentUserStatus(): MyResponse<UserStatus> {
 return      repository.getCurrentUserStatus()

    }





}