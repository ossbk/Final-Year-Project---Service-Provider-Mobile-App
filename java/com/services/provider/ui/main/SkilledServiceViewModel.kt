package com.services.provider.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.repository.MessageRepository
import com.services.provider.domain.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkilledServiceViewModel @Inject constructor(private val servicesRepository: ServicesRepository,private val messageRepository: MessageRepository) :
    ViewModel() {

    private val _getServicesResponse = MutableStateFlow<MyResponse<List<SkilledService>>?>(null)
    val getServicesResponse = _getServicesResponse.asStateFlow()
    fun getServices() {
        _getServicesResponse.value = MyResponse.Loading
        viewModelScope.launch {
            _getServicesResponse.value = servicesRepository.getAllServices()
        }
    }

    init {
        viewModelScope.launch {
            messageRepository.getAllChats().collectLatest {

            }
        }
    }
}