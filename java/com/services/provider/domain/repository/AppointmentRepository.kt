package com.services.provider.domain.repository

import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.MyResponse


interface AppointmentRepository {
    fun getAppointmentsList(): kotlinx.coroutines.flow.Flow<MyResponse<List<Appointment>>>
    suspend fun uploadAppointment(appointment: Appointment): MyResponse<Boolean>
    suspend fun uploadAppointmentRating(appointment: Appointment): MyResponse<Boolean>
    suspend fun appointmentPaid(appointment: Appointment): MyResponse<Boolean>
}