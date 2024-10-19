package com.services.provider.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserProfile
import com.services.provider.domain.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val servicesRepository: ServicesRepository
) : ViewModel() {

    private val _userProfileDetails: MutableStateFlow<MyResponse<UserProfile>> =
        MutableStateFlow(MyResponse.Idle)
    val userProfileDetails = _userProfileDetails.asStateFlow()

    fun getAllServices(user: User) {
        _userProfileDetails.value = MyResponse.Loading
        viewModelScope.launch {
            _userProfileDetails.value = servicesRepository.getAllUserDetailsAndServices(user)
        }
    }

}