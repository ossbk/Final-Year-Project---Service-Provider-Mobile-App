package com.services.provider.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class ChatModel(
    var chatID: String = "",
    val recipients: HashMap<String, Recipient> = hashMapOf(),
    var unreadMessagesCount: Int = 0,
    var messages: List<MyMessage> = listOf(),
    var unreadMessagesNew: HashMap<String, Int> = hashMapOf()
)

@Parcelize
data class Recipient(
    var userId: String = "",
    var userName: String = "",
    var profilePicture: String? = null,
    val lastMessageTime: Long = 0,
) : Parcelable