package com.services.provider.ui.main.appointments_fragment


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeInvisible
import com.services.provider.data.utils.makeVisible
import com.services.provider.data.utils.toRecepient
import com.services.provider.databinding.ItemAppointmentNewBinding
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.AppointmentStatus
import com.services.provider.domain.model.formatDateTime
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.chat.ChatActivity

class AppointmentAdapter(private val pref: MyPref) :
    ListAdapter<Appointment, AppointmentAdapter.ItemViewHolder>(DiffUtils) {
    var updateStatusClicked: ((Appointment) -> Unit)? = null
    var rateServiceClicked: ((Appointment) -> Unit)? = null
    var tvServiceProviderClicked: ((Appointment) -> Unit)? = null
    var googlePayClicked: ((Appointment) -> Unit)? = null

    val currentUser = pref.currentUser.toCurrentUser()

    inner class ItemViewHolder(val binding: ItemAppointmentNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            with(binding) {
                tvServiceTitle.text = appointment.skilledService.title
                val recipient =
                    if (pref.isCustomer()) {
                        getItem(adapterPosition).skilledService.user.toRecepient()
                    } else {
                        getItem(adapterPosition).user.toRecepient()
                    }
                tvProviderName.text = recipient.userName
                tvPricePerHour.text = "Price per hour: $${appointment.skilledService.price}"
                tvWorkingHours.text = "Working Hours: ${appointment.workingHours}"
                tvDes.text = appointment.appointmentDateTime.formatDateTime()
                Glide.with(itemView.context).load(appointment.skilledService.imageUrl)
                    .into(ivProfile)

                deliveryStatus.backgroundTintList = ContextCompat.getColorStateList(
                    root.context, appointment.appointmentStatus.getColor()
                )
                deliveryStatus.text = appointment.appointmentStatus.msg


                var isSkilled = appointment.skilledService.userId == currentUser!!.id

                if (isSkilled && !appointment.isAppointmentCompleted) {
                    updateStatus.makeVisible()
                } else
                    updateStatus.makeInvisible()
                rateNow.isVisible = !isSkilled

                rateNow.isEnabled =
                    appointment.isAppointmentCompleted

                if (appointment.isUserRated || appointment.appointmentStatus == AppointmentStatus.Rejected) {
                    rateNow.visibility = View.GONE
                }
               googlePayButton.isVisible=!appointment.isPaid && pref.isCustomer()

                tvOrderPaid.isVisible=appointment.isPaid
            }
        }

        init {
            binding.updateStatus.setOnClickListener {
                updateStatusClicked?.invoke(getItem(adapterPosition))
            }
            binding.rateNow.setOnClickListener {
                rateServiceClicked?.invoke(getItem(adapterPosition))
            }
            binding.googlePayButton.setOnClickListener {
                googlePayClicked?.invoke(getItem(adapterPosition))
            }
            itemView.setOnClickListener {
                tvServiceProviderClicked?.invoke(getItem(adapterPosition))
            }
            binding.btnChat.setOnClickListener {
                val recipient =
                    if (pref.isCustomer()) {
                        getItem(adapterPosition).skilledService.user.toRecepient()
                    } else {
                        getItem(adapterPosition).user.toRecepient()
                    }

                Intent(binding.root.context, ChatActivity::class.java).apply {
                    putExtra("receiver", recipient)
                    binding.root.context.startActivity(this)
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemAppointmentNewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffUtils : DiffUtil.ItemCallback<Appointment>() {
    override fun areContentsTheSame(
        oldItem: Appointment, newItem: Appointment
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: Appointment, newItem: Appointment
    ): Boolean {
        return oldItem.appointmentId == newItem.appointmentId
    }
}
