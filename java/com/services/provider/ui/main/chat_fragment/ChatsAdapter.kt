package com.services.provider.ui.main.chat_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.getTimeOrDate
import com.services.provider.data.utils.makeInvisible
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.ItemChatBinding
import com.services.provider.domain.model.ChatModel

class ChatsAdapter(private val prefHelper: MyPref) :
    ListAdapter<ChatModel, ChatsAdapter.AdapterVH>(ChatsDiffUtil()) {

    var onChatClick: ((ChatModel) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        return AdapterVH(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        holder.bindData(getItem(position))
    }

    inner class AdapterVH(private val binding: ItemChatBinding) :
        ViewHolder(binding.root) {
        fun bindData(item: ChatModel?) {
            item?.let { model ->
                binding.apply {
                    val currentUser = prefHelper.currentUser.toCurrentUser()!!.id
                    val receiver =
                        model.recipients.filter { it.key != currentUser }.values.firstOrNull()!!



                    userNameList.text = receiver.userName




                    if (receiver.profilePicture?.isNotBlank() == true) {
                        Glide.with(itemView.context).load(receiver.profilePicture)
                            .placeholder(R.drawable.baseline_person_4_24).into(profilePicChat)
                    }

                    if (model.unreadMessagesCount > 0) {
                        unreadMessages.makeVisible()
                        unreadMessages.text = model.unreadMessagesCount.toString()
                    } else
                        unreadMessages.makeInvisible()
                    lastMessage.text = model.messages.lastOrNull()?.message ?: ""
                    timeMain.text = model.messages.lastOrNull()?.messageDate?.getTimeOrDate() ?: ""
//                    tvLastMessage.text=model.messages.lastOrNull()?.message
                    //    tvTime.text=model.messages.lastOrNull()?.messageDate.toString()
                }
            }
        }

        init {
            itemView.setOnClickListener {
                onChatClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    class ChatsDiffUtil : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.chatID == newItem.chatID
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem

        }
    }

    override fun submitList(list: List<ChatModel>?) {
        super.submitList(ArrayList(list))
    }
}