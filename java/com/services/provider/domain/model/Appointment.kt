package com.services.provider.domain.model

import android.text.format.DateFormat
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.ui.auth.signup.isCustomer

data class Appointment(
    var appointmentId: String = "",
    val skilledService: SkilledService = SkilledService(),
    val user: User = User(),
    val workingHours: Int = 0,
    val appointmentDateTime: Long = 0,
    var appointmentStatus: AppointmentStatus = AppointmentStatus.Pending,
    var bookingTime: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    var isAppointmentCompleted: Boolean = false,
    var isUserRated: Boolean = false,
    var rating: Float = 0f,
    var ratingMsg: String = "",
    var isPaid:Boolean=false
){
    fun getRecipient(pref: MyPref): User {
        return if (pref.isCustomer()) {
            skilledService.user
        } else{
            user
        }

    }
}

enum class AppointmentStatus(val msg:String) {
    Pending("Appointment Booked and wait for confirmation"), Approved("Appointment Confirmed"), Rejected("Appointment Rejected"), Completed("Completed");

    fun getColor(): Int {
        return when (this) {
            Pending -> {
                R.color.pending
            }

            Approved -> {
                R.color.approved
            }

            Rejected -> {
                R.color.cancelled
            }

            Completed -> {
                R.color.completed
            }
        }
    }

}

fun Long.formatDateTime(): String {
    return DateFormat.format("dd MMM,yyyy hh:mm a", this).toString()
}