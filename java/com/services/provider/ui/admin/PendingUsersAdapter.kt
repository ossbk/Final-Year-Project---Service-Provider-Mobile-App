package com.services.provider.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.databinding.ItemPendingUserBinding
import com.services.provider.domain.model.User

class PendingUsersAdapter :
    ListAdapter<User, PendingUsersAdapter.ItemViewHolder>(DiffUtils) {

    var     onRejectClick: ((User) -> Unit)? = null
    var onAcceptClick: ((User) -> Unit)? = null

    inner class ItemViewHolder(val binding: ItemPendingUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                 Glide.with(itemView.context).load(user.profilePhoto)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(ivProfile)

                 tvProfileTitle.text = user.getName()
                tvUserInfo.text = user.countryName


            }
        }

        init {

            binding.ivAccept.setOnClickListener {
                onAcceptClick?.invoke(getItem(adapterPosition))
            }
            binding.ivReject.setOnClickListener {
                onRejectClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemPendingUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(it: User) {
        val list = currentList.toMutableList()
        list.remove(it)
        submitList(list)
    }
}

object DiffUtils : DiffUtil.ItemCallback<User>() {
    override fun areContentsTheSame(
        oldItem: User, newItem: User
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: User, newItem: User
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
