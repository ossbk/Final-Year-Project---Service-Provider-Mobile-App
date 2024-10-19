package com.services.provider.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.databinding.ItemAllUsersBinding
import com.services.provider.domain.model.User
import com.services.provider.domain.model.UserStatus

class AllUsersAdapter :
    ListAdapter<User, AllUsersAdapter.ItemViewHolder>(DiffUtils) {

    var onDeleteClick: ((User) -> Unit)? = null
    var onDisableClick: ((User) -> Unit)? = null
    var onItemClicked: ((User) -> Unit)? = null

    inner class ItemViewHolder(val binding: ItemAllUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                 Glide.with(itemView.context).load(user.profilePhoto)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(ivProfile)

                 tvProfileTitle.text = user.getName()
                tvUserInfo.text = user.countryName

                if (user.status == UserStatus.APPROVED) {
                    disableUser.text = "Disable"
                } else {
                    disableUser.text = "Enable"
                }



            }
        }

        init {

            binding.deleteUser.setOnClickListener {
                onDeleteClick?.invoke(getItem(adapterPosition))
            }
            binding.disableUser.setOnClickListener {
                onDisableClick?.invoke(getItem(adapterPosition))
            }

            binding.clProfile.setOnClickListener{
                onItemClicked?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemAllUsersBinding.inflate(
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

