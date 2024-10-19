package com.services.provider.ui.book_appointment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.formatDateTime
import com.services.provider.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BookAppointmentViewModel @Inject constructor(private val appointmentRepository: AppointmentRepository) :
    ViewModel() {


    val calendar = Calendar.getInstance()

    private val _bookAppointmentState = MutableStateFlow(BookAppointmentState())

    val bookAppointmentState = _bookAppointmentState.asStateFlow()

    fun updateDate(date: String, dateLong: Long) {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = dateLong
        val day = tempCalendar.get(Calendar.DAY_OF_MONTH)
        val month = tempCalendar.get(Calendar.MONTH)
        val year = tempCalendar.get(Calendar.YEAR)
        calendar.set(year, month, day)
        val formattedDate = calendar.timeInMillis.formatDateTime()
        Log.d("cvrr", "updateDate: $formattedDate")
        _bookAppointmentState.update {
            _bookAppointmentState.value.copy(date = date, dateLong = dateLong)
        }
    }

    fun updateTime(time: String, timeLong: Long) {
//        val calendar = Calendar.getInstance()
        //get calendar from timeLong
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = timeLong

        val hours = tempCalendar.get(Calendar.HOUR_OF_DAY)
        val minutes = tempCalendar.get(Calendar.MINUTE)

        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)

        val formattedDate =
            android.text.format.DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString()
        Log.d("cvrr", "updateTime: $formattedDate")
        _bookAppointmentState.update {
            _bookAppointmentState.value.copy(time = time, timeLong = timeLong)
        }
    }

    fun bookAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _bookAppointmentState.update {
                it.copy(uploadingStatus = MyResponse.Loading)
            }
            _bookAppointmentState.update {
                it.copy(uploadingStatus = appointmentRepository.uploadAppointment(appointment))
            }
        }
    }
}

data class BookAppointmentState(
    var date: String = "",
    var time: String = "",
    var dateLong: Long = 0L,
    var timeLong: Long = 0L,
    var isUploading: Boolean = false,
    var uploadingStatus: MyResponse<Boolean> = MyResponse.Idle
)