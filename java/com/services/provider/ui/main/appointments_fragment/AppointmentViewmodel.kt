package com.services.provider.ui.main.appointments_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewmodel @Inject constructor(private val appointmentRepository: AppointmentRepository) :
    ViewModel() {

  /*  private val _uploadingResult: MutableStateFlow<MyResponse<Boolean>> =
        MutableStateFlow(MyResponse.Idle)
    val uploadingResult = _uploadingResult.asStateFlow()

*/
    fun uploadAppointment(appointment: Appointment) {
        viewModelScope.launch {
        //    _uploadingResult.update {
                appointmentRepository.uploadAppointment(appointment)
         //   }
        }
    }

    fun rateAppointment(appointment: Appointment) {
        viewModelScope.launch {
         //   _uploadingResult.update {
                appointmentRepository.uploadAppointmentRating(appointment)
      //      }
        }
    }

  fun appointmentPaid(appointment: Appointment) {
        viewModelScope.launch {
         //   _uploadingResult.update {
                appointmentRepository.appointmentPaid(appointment)
      //      }
        }
    }


    val appointmentListResult = appointmentRepository.getAppointmentsList().stateIn(
        viewModelScope, SharingStarted.Eagerly,
        MyResponse.Idle
    )


}