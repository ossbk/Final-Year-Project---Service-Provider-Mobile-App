package com.services.provider.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.getTimeOrDate
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.ItemMessageBinding
import com.services.provider.domain.model.MyMessage
import com.services.provider.domain.model.User
import javax.inject.Inject

class MessagesAdapter @Inject constructor(
    prefHelper: MyPref
) :
    ListAdapter<MyMessage, MessagesAdapter.EventsViewHolder>(MessagesDiffCallback()) {
    var onEventClick: ((MyMessage) -> Unit)? = null
    var currentUser: User = prefHelper.currentUser.toCurrentUser()!!
    var currentUserId: String = currentUser.id


    // Keep track of the last message for each participant
    val lastMessages = mutableMapOf<String, MyMessage>() // Map of sender/eventId to last message


    inner class EventsViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyMessage) {
            binding.apply {

                if (currentUserId == item.sender.id) {
                    clSenderLayout.makeVisible()
                    clReceiverLayout.makeGone()
                    tvReceiverTime.makeGone()

                    tvSenderMessage.text = item.message


                    if (item.chatId.isNotEmpty()) {
                        tvSenderTime.makeGone()
                        val user = item.sender
                        Log.d("MessagesAdapter", "user: $user sender: ${item.sender.id}")
                        user?.let {
                            if (it.profilePhoto.isNotBlank()) {
                                Log.d("MessagesAdapter", "profilePhoto: ${it.profilePhoto}")
                                Glide.with(binding.root).load(it.profilePhoto)
                                    .placeholder(R.drawable.baseline_person_24).into(ivSender)
                            }

                        }
                    } else {

                        tvSenderTime.makeVisible()
                        clSenderLayout.makeGone()

                        tvSenderTime.text = item.messageDate.getTimeOrDate()
                    }

                } else {
                    clReceiverLayout.makeVisible()
                    clSenderLayout.makeGone()
                    tvSenderTime.makeGone()
                    tvReceiverMessage.text = item.message
                    if (item.chatId.isNotEmpty()) {
                        tvReceiverTime.makeGone()

//                        val user = currentUser
                        item.sender?.let {
                           if (it.profilePhoto.isNotBlank()) {
                                Log.d("MessagesAdapter", "profilePhoto: ${it.profilePhoto}")
                                Glide.with(binding.root).load(it.profilePhoto)
                                    .placeholder(R.drawable.baseline_person_4_24).into(ivReceiver)
                            }


                        }
                    } else {
                        tvReceiverTime.makeVisible()
                        clReceiverLayout.makeGone()


                        tvReceiverTime.text = item.messageDate.getTimeOrDate()
                    }
                }
            }
            //  binding.tvName.text=item.name
            itemView.setOnClickListener {
                onEventClick?.invoke(getItem(adapterPosition))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<MyMessage>?) {
        if (list != null) {
            val newList = ArrayList(list)
            var prevMessage: MyMessage? = null
            var initialIndex = 0
            list.forEachIndexed { index, message ->
                if (message.sender.id != (prevMessage?.sender?.id)
                    && prevMessage != null
                ) {

                    Log.d("MYTAG", "prevmsg = $prevMessage index=$index")
                    val newMessage = MyMessage(
                        messageID = "",
                        message = "",
                        chatId = "",
                        messageDate = prevMessage!!.messageDate,
                        sender = prevMessage!!.sender
                    )
                    //   indexWithMessages[index]=newMessage
                    newList.add(index + initialIndex, newMessage)
                    initialIndex++
                }
                prevMessage = message
            }



            if (list.isNotEmpty() && prevMessage != null) {
                val newMessage = MyMessage(
                    messageID = "",
                    message = "",
                    chatId = "",
                    messageDate = prevMessage!!.messageDate,
                    sender = prevMessage!!.sender
                )
                newList.add(newMessage)
            }

            super.submitList(newList)
        }
    }


}

class MessagesDiffCallback : DiffUtil.ItemCallback<MyMessage>() {
    override fun areItemsTheSame(oldItem: MyMessage, newItem: MyMessage): Boolean {
        return oldItem.messageID == newItem.messageID
    }

    override fun areContentsTheSame(oldItem: MyMessage, newItem: MyMessage): Boolean {
        return oldItem == newItem
    }

}