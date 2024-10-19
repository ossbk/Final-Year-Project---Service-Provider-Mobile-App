package com.services.provider.ui.add_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.repository.ServicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddServiceViewModel @Inject constructor(private val servicesRepository: ServicesRepository) :ViewModel(){

    private val _addServiceResponse=MutableStateFlow<MyResponse<Boolean>?>(null)
    val addServiceResponse=_addServiceResponse.asStateFlow()
    fun addService(service: SkilledService){
        viewModelScope.launch {
       _addServiceResponse.value= servicesRepository.addService(service)
        }
    }
    fun deleteService(service: SkilledService){
        viewModelScope.launch {
        servicesRepository.deleteService(service)
        }
    }
}