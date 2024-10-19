package com.services.provider.domain.repository

import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserProfile


interface ServicesRepository {

    suspend fun addService(skilledService: SkilledService): MyResponse<Boolean>
    suspend fun getAllServices(userId: String = ""): MyResponse<List<SkilledService>>
    suspend fun searchServices(query: String): MyResponse<List<SkilledService>>
    suspend fun getAllUserDetailsAndServices(user: User): MyResponse<UserProfile>
    suspend fun deleteAllServicesFromUser(user: User)
    suspend fun deleteService(skilledService: SkilledService)
//    suspend fun getCurrentSkilledServices(): MyResponse<List<SkilledService>>


}