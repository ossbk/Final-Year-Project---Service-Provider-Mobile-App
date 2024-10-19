package com.services.provider.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.services.provider.R
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.databinding.FragmentSignupBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.ui.dialogs.ProgressDialogUtil
import com.services.provider.ui.pending.PendingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentSignupBinding

    private val signupViewModel: SignupViewModel by viewModels()

    @Inject
    lateinit var pref: MyPref

    var photoUri: Uri? = null

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                photoUri = data?.data
                binding.userProfileImg.setImageURI(photoUri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()

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
                            mContext, "Sign up successful", Toast.LENGTH_SHORT
                        ).show()


                        val intent = Intent(
                            mContext,
                          PendingActivity::class.java
                        )


                        startActivity(intent)
                        mContext.finish()

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
            tvSignIn.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRegister.setOnClickListener {
                val email = binding.etEmail.text.toString()
                 val password = binding.etPassword.text.toString()
                val firstName = binding.etName.text.toString()
                val lastName = binding.etSrName.text.toString()
                val skilled = binding.spinner.selectedItem.toString()
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

                signupViewModel.signUp(
                    User(
                        firstName = firstName,
                        lastName = lastName,
                         email = email,
                        password = password,
                        skilledType = skilled,
                        imageUri = photoUri
                    )
                )
            }
        }
    }


}


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun MyPref.isCustomer(): Boolean {
    return this.currentUser.toCurrentUser()?.skilledType == "Customer"
}

fun User.isCustomer(): Boolean {
    return this.skilledType == "Customer"
}

fun MyPref.isSameUser(userId: String): Boolean {
    return this.currentUser.toCurrentUser()?.id == userId
}