package com.services.provider.ui.auth.login

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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState: MutableStateFlow<MyResponse<Boolean>?> = MutableStateFlow(null)
    val loginState = _loginState.asStateFlow()

    fun login(user: User) {
        _loginState.value = MyResponse.Loading
        viewModelScope.launch {
            _loginState.value = authRepository.login(user)
        }
    }

}