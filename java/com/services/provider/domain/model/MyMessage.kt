package com.services.provider.domain.model

data class MyMessage(
    val messageID: String = "",
    val message: String = "",
    var chatId: String = "",
    var messageDate: Long = 0,
    var sender: User = User(),
)