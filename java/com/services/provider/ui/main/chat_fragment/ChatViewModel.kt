package com.services.provider.ui.main.chat_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val messageRepository: MessageRepository) :
    ViewModel() {

    val allChats = messageRepository.getAllChats()
        .stateIn(viewModelScope, SharingStarted.Eagerly, MyResponse.Idle)
}