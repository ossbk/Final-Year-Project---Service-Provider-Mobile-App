package com.services.provider.ui.book_appointment

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.databinding.ActivityBookAppointmentBinding
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.dialogs.ProgressDialogUtil
import com.services.provider.ui.service_details.ServiceDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookAppointmentActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivityBookAppointmentBinding

    @Inject
    lateinit var pref: MyPref

    private val bookAppointmentViewModel: BookAppointmentViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.cvToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        with(binding) {
            cvSelectDate.setOnClickListener {
                showDatePickerDialog()
            }
            cvSelectTime.setOnClickListener {
                showTimePickerDialog()
            }

            lifecycleScope.launch {
                bookAppointmentViewModel.bookAppointmentState.flowWithLifecycle(
                    lifecycle,
                    Lifecycle.State.STARTED
                )
                    .collectLatest {
                        when (it.uploadingStatus) {
                            is MyResponse.Failure -> {
                                ProgressDialogUtil.dismissProgressDialog()
                                showToast((it.uploadingStatus as MyResponse.Failure).msg)
                            }

                            MyResponse.Idle -> {

                            }

                            MyResponse.Loading -> {
                                ProgressDialogUtil.showProgressDialog(mContext)
                            }

                            is MyResponse.Success -> {
                                ProgressDialogUtil.dismissProgressDialog()
                                showToast("Apppointment booked successfully")
                                finish()

                            }
                        }
                        //   if (it.date.isNotEmpty())
                        tvDate.text = it.date
                        //    if (it.time.isNotEmpty())
                        tvTime.text = it.time
                    }
            }

            btnConfirmAppointment.setOnClickListener {
                val text = etHours.text.toString()
                if (text.isEmpty()) {
                    showToast("Enter working hours")
                    return@setOnClickListener
                }
                val appointment = Appointment(
                    skilledService = ServiceDetailsActivity.serviceDetails,
                    user = pref.currentUser.toCurrentUser()!!,
                    workingHours = text.toInt(),
                    appointmentDateTime = bookAppointmentViewModel.calendar.timeInMillis
                )
                bookAppointmentViewModel.bookAppointment(appointment)
            }

            tvServiceTitle.text = ServiceDetailsActivity.serviceDetails.title
        }


    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPress() {
        finish()
    }
}