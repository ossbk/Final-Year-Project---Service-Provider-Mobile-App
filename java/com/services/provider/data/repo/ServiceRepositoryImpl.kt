package com.services.provider.data.repo

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.StorageReference
import com.services.provider.data.prefs.MyPref
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserProfile
import com.services.provider.domain.repository.ServicesRepository
import com.services.provider.ui.auth.signup.isCustomer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val prefRepository: MyPref,
    private val storageRef: StorageReference
) : ServicesRepository {

    private val servicesRef = firebaseFirestore.collection("Services")
    private val currentUser: User?
        get() = prefRepository.currentUser.toCurrentUser()
    var services: List<SkilledService> = emptyList()

    override suspend fun addService(skilledService: SkilledService): MyResponse<Boolean> {
        val mId = servicesRef.document().id
        if (skilledService.id == "")
            skilledService.id = mId
        skilledService.user = currentUser!!
        skilledService.userId = currentUser?.id ?: ""

        if (skilledService.imageUri != null) {
            skilledService.imageUrl =
                storageRef.uploadImage(skilledService.imageUri, skilledService.id)
        }
        return try {
            servicesRef.document(skilledService.id).set(skilledService).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }


    override suspend fun getAllServices(userId: String): MyResponse<List<SkilledService>> {
        return try {
            val query: Query = if (prefRepository.isCustomer()) {
                servicesRef
            } else {
                var id = userId
                if (userId.isBlank()) {
                    id = prefRepository.currentUser.toCurrentUser()?.id ?: ""
                }
                servicesRef
                    .whereEqualTo("userId", id)
            }
            services = query.get().await().toObjects(SkilledService::class.java)
            MyResponse.Success(services)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun getAllUserDetailsAndServices(user: User): MyResponse<UserProfile> {
        return try {
            val userId = user.id
            val query: Query = if (!user.isCustomer()) {
                servicesRef.whereEqualTo("userId", userId)
            } else {
                servicesRef.whereNotEqualTo("ratings.${user.id}", null)
            }
            val services = query.get(Source.SERVER).await().toObjects(SkilledService::class.java)
            Log.d("cvrr","Services =$services")
            val userProfile = UserProfile(user = user)
            userProfile.services=services
            if (services.isNotEmpty()) {
                if (user.isCustomer()) {
                    userProfile.ratings = services.mapNotNull {
                        Log.d("cvrr", "skillied service ratings = ${it.ratings[userId]}")

                        it.ratings[userId]
                    }.flatten()
                } else {
                    userProfile.ratings =
                        services.mapNotNull {
                            Log.d("cvrr", "skillied service ratings = ${it.ratings.values}")
                            it.ratings.values
                        }.flatten().flatten()
                }
            }
            MyResponse.Success(userProfile)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun deleteAllServicesFromUser(user: User) {
        when(val services=  getAllServices(user.id)){
            is MyResponse.Failure -> {

            }
            MyResponse.Idle -> {

            }
            MyResponse.Loading -> {

            }
            is MyResponse.Success -> {
                services.data.forEach {
                    servicesRef.document(it.id).delete()

                }
            }
        }
    }

    override suspend fun deleteService(skilledService: SkilledService) {
         servicesRef.document(skilledService.id).delete()
    }

    override suspend fun searchServices(query: String): MyResponse<List<SkilledService>> {
        return try {
            val arraylist = if (services.isNotEmpty()) {
                services
            } else {
                servicesRef.get().await().toObjects<SkilledService>()
            }

            var filteredList = arraylist.filter {
                it.title.contains(query, true) || it.serviceCategory.equals(query, true)
            }
            if (!prefRepository.isCustomer()) {
                filteredList = filteredList.filter {
                    it.user.id == prefRepository.currentUser.toCurrentUser()?.id
                }
            }
            MyResponse.Success(filteredList)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    fun updateService(service: SkilledService) {
        servicesRef.document(service.id).set(service)
    }

    /*  override suspend fun getCurrentSkilledServices(): MyResponse<List<SkilledService>> {
          return try {
              val services =
                  servicesRef
  //                .whereEqualTo("userId", prefRepository.currentUser.toCurrentUser()?.id)
                      .get().await()
                      .toObjects(SkilledService::class.java)
              MyResponse.Success(services)
          } catch (e: Exception) {
              MyResponse.Failure(e.message.toString())
          }
      }*/


}


suspend fun StorageReference.uploadImage(imageUri: Uri?, prodId: String): String {
    try {
        if (imageUri == null) return ""
        Log.d("cvrr", "Uploading images")
        var downloadUrl = ""
        val imagesRef = this.child(prodId)
        val completedJob = CoroutineScope(Dispatchers.IO).async {
            val putTask =
                imagesRef.child(System.currentTimeMillis().toString()).putFile(imageUri).await()
            downloadUrl = putTask.storage.downloadUrl.await().toString()
        }
        completedJob.await()
        Log.d("cvrr", "Uploading images completed after await")
        return downloadUrl
    } catch (e: Exception) {
        Log.d("cvrr", "error loading Exc $e")
        return ""
    }

}
