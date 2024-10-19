package com.services.provider.data.repo

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.services.provider.data.prefs.MyPref
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserRating
import com.services.provider.domain.repository.AppointmentRepository
import com.services.provider.ui.auth.signup.isCustomer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val prefRepository: MyPref,
    private val storageRef: StorageReference
) : AppointmentRepository {

    private val appointmentsRef = firebaseFirestore.collection("Appointments")
    private val servicesRef = firebaseFirestore.collection("Services")
    private val currentUser: User?
        get() = prefRepository.currentUser.toCurrentUser()

    override fun getAppointmentsList(): Flow<MyResponse<List<Appointment>>> = callbackFlow {

        val query = if (prefRepository.isCustomer()) {
            appointmentsRef.whereEqualTo("user.id", currentUser?.id)
        } else
            appointmentsRef.whereEqualTo("skilledService.userId", currentUser?.id)

        val cartListener = query
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(MyResponse.Failure(error.message.toString())).isSuccess
                }
                if (value == null) {
                    Log.d("cvrr", "No data found")

                    trySend(MyResponse.Failure("No data found")).isSuccess
                }
                val appointmentItems = value?.toObjects(Appointment::class.java)

                trySend(MyResponse.Success(appointmentItems ?: emptyList())).isSuccess
            }
        awaitClose {
            cartListener.remove()
        }
    }

    override suspend fun uploadAppointment(appointment: Appointment): MyResponse<Boolean> {
        if (appointment.appointmentId.isEmpty()) {
            val documentID = appointmentsRef.document().id
            appointment.appointmentId = documentID
            appointment.bookingTime = Timestamp.now()
        }
        return try {
            appointmentsRef.document(appointment.appointmentId).set(appointment).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            return MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun uploadAppointmentRating(appointment: Appointment): MyResponse<Boolean> {

        return try {
            appointmentsRef.document(appointment.appointmentId).set(appointment).await()
            val user = appointment.user
            val userRating = UserRating(
                userId = user.id,
                userName = user.getName(),
                userEmail = user.email,
                rating = appointment.rating,
                ratingMsg = appointment.ratingMsg,
                country = user.countryName,
            )
            val map = mapOf(
                "serviceRating" to FieldValue.increment(appointment.rating.toDouble()),
                "serviceNoOfPeopleRated" to FieldValue.increment(1),
                "ratings" to mapOf(user.id to FieldValue.arrayUnion(userRating))
            )
            servicesRef.document(appointment.skilledService.id).set(map, SetOptions.merge())
            MyResponse.Success(true)
        } catch (e: Exception) {
            return MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun appointmentPaid(appointment: Appointment): MyResponse<Boolean> {
        return try {
            appointmentsRef.document(appointment.appointmentId).set(appointment).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            return MyResponse.Failure(e.message.toString())
        }
    }


}

