package com.services.provider.ui.service_details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.toRecepient
import com.services.provider.databinding.ActivityServiceDetailsNewBinding
import com.services.provider.domain.model.SkilledService
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.book_appointment.BookAppointmentActivity
import com.services.provider.ui.chat.ChatActivity
import com.services.provider.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServiceDetailsActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivityServiceDetailsNewBinding


    @Inject
    lateinit var pref: MyPref

    companion object {
        var serviceDetails = SkilledService()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {

            ivBack.setOnClickListener {
                finish()
            }

            btnBookAppointment.setOnClickListener {
                startActivity(
                    Intent(
                        this@ServiceDetailsActivity,
                        BookAppointmentActivity::class.java
                    )
                )
            }

            tvTitle.text = serviceDetails.title
            tvPrice.text = "$" + serviceDetails.price
            tvDes.text = serviceDetails.description
            tvProfileTitle.text = serviceDetails.user.getName()
            tvUserInfo.text = serviceDetails.user.userDetails

                Glide.with(mContext).load(serviceDetails.imageUrl).into(ivService)

            clProfile.setOnClickListener {
                ProfileActivity.userDetail = serviceDetails.user
                Intent(mContext, ProfileActivity::class.java).also {
                    startActivity(it)
                }

            }
            ivCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = ("tel:" + serviceDetails.user.phoneNo).toUri()
                startActivity(intent)
            }

            ivChat.setOnClickListener {
                val recipient =
                    if (pref.isCustomer()) {
                        serviceDetails.user.toRecepient()
                    } else {
                        serviceDetails.user.toRecepient()
                    }

                Intent(binding.root.context, ChatActivity::class.java).apply {
                    putExtra("receiver", recipient)
                    binding.root.context.startActivity(this)
                }

            }

            if (serviceDetails.user.id == pref.currentUser.toCurrentUser()!!.id) {
                clProfile.makeGone()
                btnBookAppointment.makeGone()
            }
            Glide.with(mContext.applicationContext).load(serviceDetails.user.profilePhoto)
                .placeholder(com.services.provider.R.drawable.profile_icon_empty)
                .error(com.services.provider.R.drawable.profile_icon_empty)
                .into(ivProfile)

            if (serviceDetails.ratings.isNotEmpty()) {
                val ratingAdapter = RatingAdapter(pref)
                rvRatings.apply {
                    layoutManager = LinearLayoutManager(mContext)
                    adapter = ratingAdapter
                }
                val totalRAtings = serviceDetails.ratings.values.flatten()
                val rating = totalRAtings.map { it.rating }.average().roundOfToDigits(2)
                ratingAdapter.submitList(totalRAtings)
                tvRating.text = rating.toString()
                tvNoOfPeopleRated.text = "(${totalRAtings.size.toString()})"
                Log.d("cvrr","total ratings =$totalRAtings")
            }

//            tvRating.text = serviceDetails.serviceRating.toString()
//            tvNoOfRating.text = serviceDetails.serviceNoOfPeopleRated.toString()


        }

    }


    override fun onBackPress() {
        finish()
    }
}


fun Double.roundOfToDigits(n: Int): String {
    return "%.${n}f".format(this)
}
fun Float.roundOfToDigits(n: Int): String {
    return "%.${n}f".format(this)
}