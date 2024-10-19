package com.services.provider.ui.main.appointments_fragment

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.services.provider.R
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.AcceptRejectBinding
import com.services.provider.databinding.DialogTakeRatingBinding
import com.services.provider.databinding.FragmentAppointmentBinding
import com.services.provider.domain.model.Appointment
import com.services.provider.domain.model.AppointmentStatus
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class AppointmentFragment : BaseFragment() {

    @Inject
    lateinit var binding: com.services.provider.databinding.FragmentAppointmentBinding
    private lateinit var appointmentAdapter: AppointmentAdapter

    private val appointmentViewmodel: AppointmentViewmodel by viewModels()

    @Inject
    lateinit var pref: MyPref
    var lastAppointmentClicked: Appointment? = null
    private lateinit var paymentsClient: PaymentsClient

    private val resolvePaymentForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                    }

                RESULT_CANCELED -> {
                    context?.showToast("Payment Cancelled")
// The user cancelled the payment attempt
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            paymentsClient = createPaymentsClient(requireActivity())

            rvServices.apply {
                layoutManager = LinearLayoutManager(mContext)
                appointmentAdapter = AppointmentAdapter(pref)
                adapter = appointmentAdapter
            }

            appointmentAdapter.updateStatusClicked = { appointment ->
                if (appointment.appointmentStatus == AppointmentStatus.Pending) {
                    showAcceptRejectAlert {
                        appointment.appointmentStatus =
                            if (it) AppointmentStatus.Approved else AppointmentStatus.Rejected
                        if (appointment.appointmentStatus == AppointmentStatus.Rejected) {
                            appointment.isAppointmentCompleted = true
                        }
                        appointmentViewmodel.uploadAppointment(appointment)
                    }

                } else {
                    showCompletedAlert {
                        appointment.appointmentStatus = AppointmentStatus.Completed
                        appointment.isAppointmentCompleted = true
                        appointmentViewmodel.uploadAppointment(appointment)
                    }
                }
            }
            appointmentAdapter.rateServiceClicked = {
                showRatingAlert { fl, s ->
                    it.isUserRated = true
                    it.rating = fl
                    it.ratingMsg = s
                    appointmentViewmodel.rateAppointment(it)
                }
            }

            appointmentAdapter.tvServiceProviderClicked = {
                ProfileActivity.userDetail = it.getRecipient(pref)
                Intent(mContext, ProfileActivity::class.java).also {
                    startActivity(it)
                }
            }
            appointmentAdapter.googlePayClicked = {
                lastAppointmentClicked=it
                val transactionInfo = JSONObject().apply {
                    put("totalPrice", "123.45")
                    put("totalPriceStatus", "FINAL")
                    put("currencyCode", "USD")
                }

                val merchantInfo = JSONObject().apply {
                    put("merchantName", "Example Merchant")
                    put("merchantId", "01234567890123456789")
                }

                val paymentDataRequestJson =
                    JSONObject(googlePayBaseConfiguration.toString()).apply {
                        put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod))
                        put("transactionInfo", transactionInfo)
                        put("merchantInfo", merchantInfo)
                    }

                val paymentDataRequest =
                    PaymentDataRequest.fromJson(paymentDataRequestJson.toString())


                val task = paymentsClient.loadPaymentData(paymentDataRequest)
                task.addOnCompleteListener { completedTask ->
                    if (completedTask.isSuccessful) {
                        completedTask.result.let(::handlePaymentSuccess)
                    } else {
                        when (val exception = completedTask.exception) {
                            is ResolvableApiException -> {
                                resolvePaymentForResult.launch(
                                    IntentSenderRequest.Builder(exception.resolution).build()
                                )
                            }

                            is ApiException -> {
                                handleError(exception.statusCode, exception.message.toString())
                            }

                            else -> {
                                handleError(
                                    CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                            " exception when trying to deliver the task result to an activity!"
                                )
                            }
                        }
                    }
                }
            }

            getAppointmentsList()
        }


    }

    private fun FragmentAppointmentBinding.getAppointmentsList() =
        lifecycleScope.launch {
            appointmentViewmodel.appointmentListResult.collectLatest {
                when (it) {
                    is MyResponse.Failure -> {
                        pbLoading.visibility = View.VISIBLE
                        Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                    }

                    MyResponse.Idle -> {

                    }

                    MyResponse.Loading -> {
                        pbLoading.visibility = View.VISIBLE
                    }

                    is MyResponse.Success -> {
                        pbLoading.visibility = View.GONE
                        appointmentAdapter.submitList(it.data)
                        appointmentAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    private fun showAcceptRejectAlert(updateStatus: (Boolean) -> Unit) {
        val binding = AcceptRejectBinding.inflate(layoutInflater, null, false)
        val dialog = MaterialAlertDialogBuilder(mContext).setTitle("Change order status")
            .setView(binding.root)
            .show()

        binding.btnUpdateStatus.setOnClickListener {
            if (binding.rbStatusGroup.checkedRadioButtonId == R.id.rbAccept) {
                updateStatus(true)
            } else
                updateStatus(false)
            dialog.dismiss()
        }
    }

    fun showCompletedAlert(completed: () -> Unit) {
        MaterialAlertDialogBuilder(mContext).setTitle("Mark Completed")
            .setMessage("Do you want to mark order as completed.")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                completed()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun showRatingAlert(completed: (Float, String) -> Unit) {
        val binding = DialogTakeRatingBinding.inflate(layoutInflater, null, false)
        val dialog = MaterialAlertDialogBuilder(mContext).setTitle("Rate Service")
            .setView(binding.root)
            .show()

        binding.btnRateNow.setOnClickListener {
            if (binding.ratingBar.rating == 0.0f) {
                mContext.showToast("Please select rating")
                return@setOnClickListener
            }
            completed(binding.ratingBar.rating, binding.etMsg.text.toString())
            dialog.dismiss()
        }
    }


    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    private val baseCardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("parameters", JSONObject().apply {
            put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
            put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
        })
    }

    private val googlePayBaseConfiguration = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
        put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod))
    }

    private val tokenizationSpecification = JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put(
            "parameters", JSONObject(
                mapOf(
                    "gateway" to "example",
                    "gatewayMerchantId" to "exampleGatewayMerchantId"
                )
            )
        )
    }

    private val cardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("tokenizationSpecification", tokenizationSpecification)
        put("parameters", JSONObject().apply {
            put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD")))
            put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
            put("billingAddressRequired", true)
            put("billingAddressParameters", JSONObject(mapOf("format" to "FULL")))
        })
    }


    private fun handleError(statusCode: Int, message: String) {
        context?.showToast(message)
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return
        try {
// Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)
            Log.d("PaymentData", paymentInformation)
// Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()
// Logging token string.
            Log.d(
                "GooglePaymentToken", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )
            lastAppointmentClicked?.let {
it.isPaid=true
                appointmentViewmodel.appointmentPaid(it)
                binding.getAppointmentsList()
            }
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }
    }
}