package com.services.provider.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.utils.toRecepient
import com.services.provider.domain.model.ChatModel
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserRating
import com.services.provider.domain.model.UserStatus
import com.services.provider.domain.repository.AuthRepository
import com.services.provider.ui.chat.ChatViewModel_Factory
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val prefRepository: MyPref,
    private val storageReference: StorageReference,
   private val serviceRepositoryImpl: ServiceRepositoryImpl,
    private val appointmentRepositoryImpl: AppointmentRepositoryImpl,
    private val messageRepositoryImpl: MessageRepositoryImpl
) : AuthRepository {

    private val usersRef = firebaseFirestore.collection("Users")
    private val servicesRef = firebaseFirestore.collection("Services")
    override val currentUser: User?
        get() = prefRepository.currentUser.toCurrentUser()

    override suspend fun signUp(user: User): MyResponse<Boolean> {
        return try {
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).await()
            val userId = authResult.user!!.uid
            if (user.imageUri != null) {
                user.profilePhoto = storageReference.uploadImage(user.imageUri!!, userId)
            }
            user.id = userId
            usersRef.document(userId).set(user).await()
            prefRepository.currentUser = user.convertToString()
            delay(1000)
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun updateDetails(user: User): MyResponse<Boolean> {
        return try {

            if (user.imageUri != null) {
                user.profilePhoto = storageReference.uploadImage(user.imageUri!!, user.id)
            }
            usersRef.document(user.id).set(user)
            user.imageUri=null
            prefRepository.currentUser = user.convertToString()
            val services: MyResponse<List<SkilledService>> =serviceRepositoryImpl.getAllServices(user.id)
            when(services){
                is MyResponse.Failure ->{

                }
                MyResponse.Idle ->{

                }
                MyResponse.Loading ->{

                }
                is MyResponse.Success ->{
                    for (service in services.data){
                        if (service.user.id==user.id) {
                            service.user = user
                        }
                      service.ratings.get(user.id)?.forEach { _ ->
                          val newData=service.ratings[user.id]
                          val result: List<UserRating>? = newData?.map {
                                it.updateUserDetails(user)
                            }
                          if (result!=null) {
                              service.ratings[user.id] = result
                          }
                       }
                        serviceRepositoryImpl.updateService(service)
                    }
                }
            }
             val chatss: MyResponse<List<ChatModel>> =messageRepositoryImpl.getAllChatsById(user.id)
            when(chatss){
                is MyResponse.Failure ->{

                }
                MyResponse.Idle ->{

                }
                MyResponse.Loading ->{

                }
                is MyResponse.Success ->{
                    for (chats in chatss.data){
                        chats.recipients[user.id]=user.toRecepient()
                        for (message in chats.messages){
                            if (message.sender.id==user.id){
                                message.sender=user
                            }
                        }
                        messageRepositoryImpl.updateChat(chats)
                    }
                }
            }
            delay(1000)
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun login(user: User): MyResponse<Boolean> {
        return try {
            val authResult =
                firebaseAuth.signInWithEmailAndPassword(user.email, user.password).await()
            val userId = authResult.user!!.uid
            user.id = userId
            val doc = usersRef.document(userId).get().await()
            prefRepository.currentUser = doc.toObject(User::class.java)?.convertToString() ?: ""
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }


    override suspend fun forgetPassword(user: User): MyResponse<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(user.email).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun getPendingUsers(): MyResponse<List<User>> {
        return try {
            val snapshot = usersRef.whereEqualTo("status", UserStatus.PENDING).get().await()
            val list = snapshot.toObjects(User::class.java)
            MyResponse.Success(list)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun approveUser(user: User): MyResponse<Boolean> {
        return try {
            usersRef.document(user.id).update("status", UserStatus.APPROVED).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun rejectUser(user: User): MyResponse<Boolean> {
        return try {
            usersRef.document(user.id).update("status", UserStatus.REJECTED).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun deleteUser(user: User): MyResponse<Boolean> {
        return try {
            serviceRepositoryImpl.deleteAllServicesFromUser(user)
            usersRef.document(user.id).update("status", UserStatus.DELETED).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun disableUser(user: User): MyResponse<Boolean> {
        return try {
            usersRef.document(user.id).update("status", UserStatus.DISABLED).await()
            MyResponse.Success(true)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun getActiveUsers(): MyResponse<List<User>> {
        return try {
            val snapshot = usersRef.whereNotEqualTo("status", UserStatus.DELETED).get().await()
            var list = snapshot.toObjects(User::class.java)
            list=list.filter { it.status != UserStatus.REJECTED && it.status != UserStatus.PENDING }
            MyResponse.Success(list)
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }

    override suspend fun getCurrentUserStatus(): MyResponse<UserStatus> {
        return try {
            val mCurrentUser = currentUser
            if (mCurrentUser != null) {
                val doc = usersRef.document(mCurrentUser.id).get().await()
                val user = doc.toObject(User::class.java)
                if (user != null) {
                    MyResponse.Success(user.status)
                } else {
                    MyResponse.Failure("User not found")
                }
            } else {
                MyResponse.Failure("User not found")
            }
        } catch (e: Exception) {
            MyResponse.Failure(e.message.toString())
        }
    }
}

fun String.toCurrentUser(): User? {
    return try {
        val gson = Gson()
        gson.fromJson(this, User::class.java)
    } catch (e: Exception) {
        null
    }
}

fun User.convertToString(): String {
    return try {
        val gson = Gson()
        gson.toJson(this)
    } catch (e: Exception) {
        ""
    }
}
