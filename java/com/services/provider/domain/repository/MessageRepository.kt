package com.services.provider.domain.repository

import com.services.provider.domain.model.ChatModel
import com.services.provider.domain.model.MyMessage
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.Recipient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface MessageRepository {
    val chatsHashMapFlow: MutableStateFlow<MyResponse<HashMap<String, ChatModel>>>

    fun sendMessage(eventId: Recipient, message: String): Flow<MyResponse<Boolean>>

    fun getAllMessages(eventId: String): Flow<MyResponse<List<MyMessage>>>
      fun getAllChats(): Flow<MyResponse<List<ChatModel>>>
    fun readMessages(eventId: String, id: String)
}