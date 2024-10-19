package com.services.provider.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
 import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpState: MutableStateFlow<MyResponse<Boolean>?> = MutableStateFlow(null)
    val signUpState = _signUpState.asStateFlow()

    fun updateDetails(user: User) {
        _signUpState.value = MyResponse.Loading
        viewModelScope.launch {
            _signUpState.value = authRepository.updateDetails(user)
        }
    }

    fun signUp(user: User) {
        _signUpState.value = MyResponse.Loading
        viewModelScope.launch {
            _signUpState.value = authRepository.signUp(user)
        }
    }

}