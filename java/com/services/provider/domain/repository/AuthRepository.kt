package com.services.provider.domain.repository

import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserStatus


interface AuthRepository {

    val currentUser: User?
    suspend fun signUp(user: User): MyResponse<Boolean>
    suspend fun updateDetails(user: User): MyResponse<Boolean>
    suspend fun login(user: User): MyResponse<Boolean>
    suspend fun forgetPassword(user: User): MyResponse<Boolean>
    suspend fun getPendingUsers(): MyResponse<List<User>>
    suspend fun approveUser(user: User): MyResponse<Boolean>
    suspend fun rejectUser(user: User): MyResponse<Boolean>
    suspend fun deleteUser(user: User): MyResponse<Boolean>
    suspend fun disableUser(user: User): MyResponse<Boolean>
    suspend fun getActiveUsers(): MyResponse<List<User>>

    suspend fun getCurrentUserStatus(): MyResponse<UserStatus>


}