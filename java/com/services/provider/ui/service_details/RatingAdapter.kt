package com.services.provider.ui.service_details


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeGone
import com.services.provider.databinding.ItemProfileRatingBinding
import com.services.provider.domain.model.UserRating

class RatingAdapter(pref: MyPref) :
    ListAdapter<UserRating, RatingAdapter.ItemViewHolder>(DiffUtils) {
    var updateStatusClicked: ((UserRating) -> Unit)? = null
    var rateServiceClicked: ((UserRating) -> Unit)? = null

    val currentUser = pref.currentUser.toCurrentUser()

    inner class ItemViewHolder(val binding: ItemProfileRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userRating: UserRating) {
            with(binding) {
                tvProfileTitle.text = userRating.userName
                tvCountry.text = userRating.country
                if (userRating.country.isBlank()) {
                    tvCountry.makeGone()
                }
                tvUserFeedback.text = userRating.ratingMsg
                tvRating.text = userRating.rating.toString()
                Glide.with(binding.root).load(userRating.userProfilePic)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(ivProfile)

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemProfileRatingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffUtils : DiffUtil.ItemCallback<UserRating>() {
    override fun areContentsTheSame(
        oldItem: UserRating, newItem: UserRating
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: UserRating, newItem: UserRating
    ): Boolean {
        return oldItem == newItem
    }
}
