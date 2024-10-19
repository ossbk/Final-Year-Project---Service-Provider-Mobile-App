package com.services.provider.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.ItemServiceNewMainBinding
import com.services.provider.domain.model.SkilledService
import com.services.provider.ui.auth.signup.isSameUser
import com.services.provider.ui.service_details.roundOfToDigits

class MainServicesAdapter(private val pref: MyPref) :
    ListAdapter<SkilledService, MainServicesAdapter.ItemViewHolder>(DiffUtils) {

    var editClicked: ((SkilledService) -> Unit)? = null
    var onItemClick: ((SkilledService) -> Unit)? = null

    inner class ItemViewHolder(val binding: ItemServiceNewMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skilledService: SkilledService) {
            with(binding) {
                tvTitle.text = skilledService.title
                tvPrice.text = "$" + skilledService.price
                Glide.with(itemView.context).load(skilledService.imageUrl).into(ivService)
                Glide.with(itemView.context).load(skilledService.user.profilePhoto)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(ivProfile)
                tvRating.text =
                    (skilledService.serviceRating / skilledService.serviceNoOfPeopleRated).roundOfToDigits(
                        2
                    ).toString()
                tvNoOfPeopleRated.text = "(${skilledService.serviceNoOfPeopleRated})"
                ivEdit.isVisible = pref.isSameUser(skilledService.userId)
                clProfile.isVisible = !pref.isSameUser(skilledService.userId)
                tvProfileTitle.text = skilledService.user.getName()
                tvUserInfo.text = skilledService.user.countryName

            }
        }

        init {
            binding.ivEdit.setOnClickListener {
                editClicked?.invoke(getItem(adapterPosition))
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemServiceNewMainBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffUtils : DiffUtil.ItemCallback<SkilledService>() {
    override fun areContentsTheSame(
        oldItem: SkilledService, newItem: SkilledService
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: SkilledService, newItem: SkilledService
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
