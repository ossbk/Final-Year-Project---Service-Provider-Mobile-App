package com.services.provider.ui.main.search

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
class SearchServicesViewModel @Inject constructor(private val servicesRepository: ServicesRepository) :
    ViewModel() {

    private val _searchedResult: MutableStateFlow<MyResponse<List<SkilledService>>> =
        MutableStateFlow(MyResponse.Idle)

    val searchResult = _searchedResult.asStateFlow()
    fun searchServices(query: String) {
        _searchedResult.value = MyResponse.Loading
        viewModelScope.launch {
            _searchedResult.value = servicesRepository.searchServices(query)
        }
    }


}