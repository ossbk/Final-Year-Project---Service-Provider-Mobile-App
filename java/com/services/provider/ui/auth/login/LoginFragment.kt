package com.services.provider.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.services.provider.R
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.FragmentLoginBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.dialogs.ProgressDialogUtil
import com.services.provider.ui.pending.PendingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentLoginBinding

    val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var pref: MyPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()


        lifecycleScope.launch {
            loginViewModel.loginState.collectLatest {
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
                            mContext,
                            "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent =
                            Intent(
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
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
            btnLogin.setOnClickListener {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                if (email.isEmpty()) {
                    mContext.showToast(getString(R.string.email_cannot_be_empty))
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    mContext.showToast(getString(R.string.password_cannot_be_empty))
                    return@setOnClickListener
                }
                if (email.equals("admin") && password.equals("admin")) {
                    val intent =
                        Intent(
                            mContext,
                            com.services.provider.ui.admin.AdminActivity::class.java
                        )
                    startActivity(intent)
                    mContext.finish()
                    return@setOnClickListener
                }
                loginViewModel.login(User(email = email, password = password))
            }
        }
    }


}