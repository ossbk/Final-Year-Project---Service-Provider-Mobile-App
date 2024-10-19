package com.services.provider.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.MessageRepositoryImpl
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.parcelable
import com.services.provider.databinding.ActivityChatBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.Recipient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : BaseActivity() {

    @Inject
    lateinit var binding: ActivityChatBinding

    private val chatViewModel:ChatViewModel by viewModels()


    @Inject
    lateinit var messagesAdapter: MessagesAdapter

    @Inject
    lateinit var prefHelper: MyPref
    var isBackPressed = false
    var chatId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val receiver = intent.parcelable<Recipient>("receiver")!!
        binding.tvUserName.text = receiver.userName
        /* val chatId =
             (prefHelper.currentUser.toCurrentUser().userId + receiver.userId).toCharArray().sorted()
                 .joinToString("")*/

        chatId =
            getUniqueChatKey(prefHelper.currentUser.toCurrentUser()!!.id, receiver.userId)


        var isFirstTimeFetch = true

        var olderMessagesSize = 0


        with(binding) {
            ivBack.setOnClickListener {
                isBackPressed = true
                finish()
            }

            rvMessages.apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext).run {
                    stackFromEnd = true
                    this
                }
                adapter = messagesAdapter
            }

            lifecycleScope.launch {
                chatViewModel.chatsHashMapFlow.collectLatest {
                    when (it) {
                        is MyResponse.Loading -> {
                            Log.d("ChatActivity", "onCreate: loading")
                        }

                        is MyResponse.Failure -> {
                            Log.d("ChatActivity", "Error: ${it.msg}")
                        }

                        is MyResponse.Success -> {
                            it.data?.values?.forEach {
                                Log.d("ChatActivity", "onCreate: \n\n")
                                Log.d("ChatActivity", "\n\nDatta: ${it}")
                            }
                            Log.d("ChatActivity", "SUCEESSS")
                            Log.d(
                                "ChatActivity",
                                "onCreate:chatid=$chatId ${it.data?.get(chatId)?.messages}"
                            )
                            messagesAdapter.submitList(it.data?.get(chatId)?.messages ?: listOf())

                            Log.d(
                                "ChatActivity",
                                "olderMessages size=$olderMessagesSize , current size=${
                                    it.data?.get(chatId)?.messages?.size
                                }"
                            )

                            olderMessagesSize = it.data?.get(chatId)?.messages?.size ?: 0

                            if (isFirstTimeFetch) {
                                rvMessages.scrollToPosition(messagesAdapter.itemCount - 1)
                                isFirstTimeFetch = false
                            }
                        }

                        MyResponse.Idle -> {

                        }
                    }
                }
            }

            ivSend.setOnClickListener {
                val message = etMessage.text.toString()
                if (message.isNotEmpty()) {
                    lifecycleScope.launch {
                        chatViewModel.sendMessage(receiver, message).collectLatest {
                            when (it) {
                                is MyResponse.Loading -> {
                                    isFirstTimeFetch = true
                                    Log.d("ChatActivity", "onCreate: Sending")
                                    etMessage.setText("")

                                }

                                is MyResponse.Failure -> {

                                }

                                is MyResponse.Success -> {
                                }

                                MyResponse.Idle -> {

                                }
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onBackPress() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        isBackPressed = false
    }

    override fun onPause() {
        super.onPause()
        isBackPressed = true
    }

    override fun onStop() {
        super.onStop()
        chatViewModel.readMessages(
            chatId,
            prefHelper.currentUser.toCurrentUser()!!.id
        )
    }
}

fun getUniqueChatKey(sender: String, receiver: String): String {
    return (sender + receiver).toCharArray().sorted().joinToString("")
}