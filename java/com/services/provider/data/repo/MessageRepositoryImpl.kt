package com.services.provider.data.repo

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.services.provider.data.prefs.MyPref
import com.services.provider.domain.model.ChatModel
import com.services.provider.domain.model.MyMessage
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.Recipient
import com.services.provider.domain.repository.MessageRepository
import com.services.provider.ui.chat.getUniqueChatKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


const val CHAT_DOC_REF = "Chats"

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val firebase: FirebaseFirestore, private val prefHelper: MyPref
) : MessageRepository {
    private val chatRef = firebase.collection(CHAT_DOC_REF)

    var chatsHashMap = hashMapOf<String, ChatModel>()
    override var chatsHashMapFlow: MutableStateFlow<MyResponse<HashMap<String, ChatModel>>> =
        MutableStateFlow(MyResponse.Loading)


    override fun readMessages(eventId: String, id: String) {
        try {
            Log.d("TAG", "uploadEventDetails: ")
            //emit(MyEvent.Loading())
            val data: MutableMap<String, Any> = HashMap()
            data["recipients.${prefHelper.currentUser.toCurrentUser()!!.id}.lastMessageTime"] =
                Timestamp.now().seconds * 1000
            chatRef.document(eventId).update(
                data
            )
        } catch (e: Exception) {
            Log.d("TAG", "uploadEventDetails: ${e.message}")
        }
    }

    override fun sendMessage(receiver: Recipient, message: String): Flow<MyResponse<Boolean>> =
        flow {
            try {
                val receiverId = receiver.userId
                emit(MyResponse.Loading)
                /*  val chatId =
                      (prefHelper.currentUser.toCurrentUser().id + receiverId).toCharArray().sorted()
                          .joinToString("")*/
                val chatId =
                    getUniqueChatKey(prefHelper.currentUser.toCurrentUser()!!.id, receiverId)
                val messageId = chatRef.document(chatId).collection("messages").document().id


                //  val getChat=chatRef.document(chatId).get().await()
                val myMessage = MyMessage(
                    messageId,
                    message,
                    chatId,
                    Timestamp.now().seconds * 1000,
                    prefHelper.currentUser.toCurrentUser()!!
                )

                if (chatsHashMap[chatId] == null) {
                    val messagesList = arrayListOf<MyMessage>()
                    messagesList.add(myMessage)
                    Log.d("TAG", "sendMessage: null ${chatsHashMap[chatId]}")
                    val recipientsHashmap = hashMapOf<String, Recipient>()
                    recipientsHashmap[prefHelper.currentUser.toCurrentUser()!!.id] =
                        Recipient(
                            prefHelper.currentUser.toCurrentUser()!!.id,
                            prefHelper.currentUser.toCurrentUser()!!.getName(),
                            prefHelper.currentUser.toCurrentUser()!!.password,
                            Timestamp.now().seconds * 1000,
                        )
                    recipientsHashmap[receiverId] =
                        receiver
                    val chatModel = ChatModel(
                        chatId,
                        recipientsHashmap,
                        0,
                        messagesList,
                    )

                    chatModel.unreadMessagesNew[prefHelper.currentUser.toCurrentUser()!!.id] = 0
                    chatModel.unreadMessagesNew[receiverId] = 1
                    chatRef.document(chatId).set(chatModel).addOnSuccessListener {
                        Log.d("TAG", "sendMessage: Success  ")
                    }
                        .addOnFailureListener {
                            Log.d("TAG", "sendMessage: Failure  ")
                        }
                    emit(MyResponse.Success(true))
                } else {
                    Log.d("TAG", "sendMessage: not null ${chatsHashMap[chatId]}")

                    val updateData: MutableMap<String, Any> = HashMap()
                    updateData["messages"] = FieldValue.arrayUnion(myMessage)
                    updateData["recipients.${prefHelper.currentUser.toCurrentUser()!!.id}.lastMessageTime"] =
                        Timestamp.now().seconds * 1000

                    //update unread count
                    updateData["unreadMessagesNew.${prefHelper.currentUser.toCurrentUser()!!.id}"] =
                        FieldValue.increment(0)
                    updateData["unreadMessagesNew.${receiverId}"] =
                        FieldValue.increment(1)
                    chatRef.document(chatId)
                        .update(updateData).addOnSuccessListener {
                            Log.d("TAG", "sendMessage: update Success  ")
                        }
                        .addOnFailureListener {
                            Log.d("TAG", "sendMessage: update Failure  ")
                        }
                    emit(MyResponse.Success(true))
                }


            } catch (e: Exception) {
                Log.d("TAG", "sendMessage: ${e.message}")
                emit(MyResponse.Failure(e.message.toString()))
            }
        }

    override fun getAllMessages(eventId: String): Flow<MyResponse<List<MyMessage>>> = flow {
        try {
            emit(MyResponse.Loading)
            val doc = chatRef.document(eventId).get().await()
            val hashmap: HashMap<String, MyMessage> =
                doc.data?.get("messagesMap") as HashMap<String, MyMessage>
            emit(MyResponse.Success(hashmap.values.toList()))
        } catch (e: Exception) {
            emit(MyResponse.Failure(e.message.toString()))
        }
    }

    private fun getUnreadCount(
        lastMessageTime: Long,
        messagesList: List<MyMessage>
    ): Int {
        Log.d("TAG", "getUnreadCount: 1")
        Log.d("TAG", "getUnreadCount: 2")
        val unreadMessages = messagesList.filter {
            it.messageDate > lastMessageTime
        }
        Log.d("TAG", "getUnreadCount: 3")

        return unreadMessages.size
    }


    override fun getAllChats(): Flow<MyResponse<List<ChatModel>>> = callbackFlow {

        trySend(MyResponse.Loading).isSuccess

        val listener: ListenerRegistration = chatRef
            .whereEqualTo(
                "recipients.${prefHelper.currentUser.toCurrentUser()!!.id}.userId",
                prefHelper.currentUser.toCurrentUser()!!.id
            )
            .addSnapshotListener { value, error ->
                chatsHashMap.clear()
                val chatsList = mutableListOf<ChatModel>()
                if (error != null) {
                    Log.e("MessageRepositoryImpl", "getAllChats: ${error.message}")

                    trySend(MyResponse.Failure(error.message.toString())).isSuccess
                } else {
                    Log.e("MessageRepositoryImpl", "getAllChats: ${value?.documents?.size}")
                    val myychats: List<ChatModel> =
                        value?.toObjects(ChatModel::class.java) ?: listOf()
                    for (doc in myychats) {

                        val lastMessageTime =
                            doc.recipients[prefHelper.currentUser.toCurrentUser()!!.id]?.lastMessageTime
                                ?: 0
                        val unreadMessagesCount = getUnreadCount(lastMessageTime, doc.messages)
                        doc.unreadMessagesCount = unreadMessagesCount
                        chatsHashMap[doc.chatID] = doc
                        chatsList.add(doc)

                    }
                    Log.d("MessageRepositoryImpl", "getAllChatsHashmap: ${chatsHashMap.keys.size}")
                    chatsHashMapFlow.update { (MyResponse.Success(chatsHashMap)) }
                    Log.d("MessageRepositoryImpl", "CHstsHashMapFlow: ${chatsHashMapFlow.value}")
                    trySend(MyResponse.Success(chatsList)).isSuccess

                }
            }

        awaitClose {
            listener.remove()
        }

    }

    suspend fun getAllChatsById(id: String): MyResponse<List<ChatModel>> {

        try {
            val value = chatRef
                .whereEqualTo(
                    "recipients.${id}.userId",
                   id
                )
                .get().await()

            chatsHashMap.clear()
            val chatsList = mutableListOf<ChatModel>()

            Log.e("MessageRepositoryImpl", "getAllChats: ${value?.documents?.size}")
            val myychats: List<ChatModel> =
                value?.toObjects(ChatModel::class.java) ?: listOf()
            for (doc in myychats) {

                val lastMessageTime =
                    doc.recipients[prefHelper.currentUser.toCurrentUser()!!.id]?.lastMessageTime
                        ?: 0
                val unreadMessagesCount = getUnreadCount(lastMessageTime, doc.messages)
                doc.unreadMessagesCount = unreadMessagesCount
                chatsHashMap[doc.chatID] = doc
                chatsList.add(doc)

            }
            Log.d("MessageRepositoryImpl", "getAllChatsHashmap: ${chatsHashMap.keys.size}")
            chatsHashMapFlow.update { (MyResponse.Success(chatsHashMap)) }
            Log.d("MessageRepositoryImpl", "CHstsHashMapFlow: ${chatsHashMapFlow.value}")
            return (MyResponse.Success(chatsList))
        } catch (e: Exception) {
            return MyResponse.Failure(e.message.toString())
        }

    }

    fun updateChat(chats: ChatModel) {
        chatRef.document(chats.chatID).set(chats)
    }
}



