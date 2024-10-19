package com.services.provider.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.Recipient
import com.services.provider.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val messageRepository: MessageRepository) :
    ViewModel() {
    fun sendMessage(receiver: Recipient, message: String): Flow<MyResponse<Boolean>> {
        return messageRepository.sendMessage(receiver, message)
    }

    fun readMessages(chatId: String, id: String) {
        messageRepository.readMessages(chatId, id)
    }

    val chatsHashMapFlow = messageRepository.chatsHashMapFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MyResponse.Idle
    )


}