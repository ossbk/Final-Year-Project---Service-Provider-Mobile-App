package com.services.provider.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.databinding.ActivityUpdateProfileBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.ui.auth.signup.SignupViewModel
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.dialogs.ProgressDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfieActivity : BaseActivity() {


    @Inject
    lateinit var binding: ActivityUpdateProfileBinding

    @Inject
    lateinit var pref: MyPref

    private val signupViewModel: SignupViewModel by viewModels()


    var photoUri: Uri? = null

    val user: User? by lazy {
        pref.currentUser.toCurrentUser()
    }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                photoUri = data?.data

                Glide.with(this@UpdateProfieActivity)
                    .load(photoUri)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(binding.userProfileImg)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        handleClicks()

        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
            inputFeildAdmin.isVisible=!pref.isCustomer()
            inputFeildCustomer.isVisible=pref.isCustomer()
            if (pref.isCustomer()) {

                binding.etName.setText(user?.firstName)
                binding.etSrName.setText(user?.lastName)
                binding.etCountryName.setText(user?.countryName)
                binding.etEmail.setText(user?.email)
                binding.etPassword.setText(user?.password)
                Glide.with(this@UpdateProfieActivity)
                    .load(user?.profilePhoto)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(userProfileImg)
            } else {

                binding.etNameAdmin.setText(user?.firstName)
                binding.etSrNameAdmin.setText(user?.lastName)
                binding.etCountryNameAdmin.setText(user?.countryName)
                binding.etEmailAdmin.setText(user?.email)
                binding.etPasswordAdmin.setText(user?.password)
                binding.etUserQualificationAdmin.setText(user?.qualification)
                binding.etUserDetailsAdmin.setText(user?.userDetails)
                Glide.with(this@UpdateProfieActivity)
                    .load(user?.profilePhoto)
                    .placeholder(R.drawable.profile_icon_empty)
                    .error(R.drawable.profile_icon_empty)
                    .into(userProfileImg)
            }
        }

        lifecycleScope.launch {
            signupViewModel.signUpState.collectLatest {
                when (it) {
                    is MyResponse.Failure -> {
                        Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    MyResponse.Loading -> {
                        ProgressDialogUtil.showProgressDialog(mContext)
                    }

                    is MyResponse.Success -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        Toast.makeText(
                            mContext, "Detais Updated successful", Toast.LENGTH_SHORT
                        ).show()


                    }

                    else -> {

                    }
                }

            }

        }
    }

    private fun handleClicks() {
        with(binding) {
            ivPickPhoto.setOnClickListener {
                imageResultLauncher.launch(Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                })
            }

            btnUpdate.setOnClickListener {

                if (pref.isCustomer()) {
                    val firstName = binding.etName.text.toString()
                    val lastName = binding.etSrName.text.toString()
                    val countryName = binding.etCountryName.text.toString()
                    val email = binding.etEmail.text.toString()
                    val password = binding.etPassword.text.toString()

                    if (firstName.isEmpty()) {
                        mContext.showToast(getString(R.string.first_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (lastName.isEmpty()) {
                        mContext.showToast(getString(R.string.last_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (email.isEmpty()) {
                        mContext.showToast(getString(R.string.email_cannot_be_empty))
                        return@setOnClickListener
                    }

                    if (password.isEmpty()) {
                        mContext.showToast(getString(R.string.password_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (countryName.isEmpty()) {
                        mContext.showToast(getString(R.string.country_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    user?.countryName = countryName
                    user?.password = password
                    user?.email = email
                    user?.firstName = firstName
                    user?.lastName = lastName


                } else {
                    val firstName = binding.etNameAdmin.text.toString()
                    val lastName = binding.etSrNameAdmin.text.toString()
                    val countryName = binding.etCountryNameAdmin.text.toString()
                    val email = binding.etEmailAdmin.text.toString()
                    val password = binding.etPasswordAdmin.text.toString()
                    val qualification = binding.etUserQualificationAdmin.text.toString()
                    val aboutYourself = binding.etUserDetailsAdmin.text.toString()

                    if (firstName.isEmpty()) {
                        mContext.showToast(getString(R.string.first_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (lastName.isEmpty()) {
                        mContext.showToast(getString(R.string.last_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (email.isEmpty()) {
                        mContext.showToast(getString(R.string.email_cannot_be_empty))
                        return@setOnClickListener
                    }

                    if (password.isEmpty()) {
                        mContext.showToast(getString(R.string.password_cannot_be_empty))
                        return@setOnClickListener
                    }
                    if (countryName.isEmpty()) {
                        mContext.showToast(getString(R.string.country_name_cannot_be_empty))
                        return@setOnClickListener
                    }
                    user?.countryName = countryName
                    user?.password = password
                    user?.email = email
                    user?.firstName = firstName
                    user?.lastName = lastName
                    user?.qualification = qualification
                    user?.userDetails = aboutYourself

                }
                if (photoUri!=null){
                    user?.imageUri=photoUri
                }
                user?.let {
                    signupViewModel.updateDetails(it)
                }

            }
        }
    }


    override fun onBackPress() {
        finish()
    }
}

