package com.services.provider.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.services.provider.R
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.databinding.FragmentPendingUsersBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.UserStatus
import com.services.provider.domain.model.serviceCategories
import com.services.provider.ui.add_service.AddServiceActivity
import com.services.provider.ui.auth.AuthActivity
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.main.category.CategoryActivity
import com.services.provider.ui.profile.ProfileActivity
import com.services.provider.ui.service_details.ServiceDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PendingUsersFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentPendingUsersBinding

    @Inject
    lateinit var pref: MyPref

    private lateinit var pendingUsersAdapter: PendingUsersAdapter
     private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            usersViewModel.getPendingUsers()

            rvUsers.apply {
                pendingUsersAdapter = PendingUsersAdapter()
                adapter = pendingUsersAdapter
                layoutManager = LinearLayoutManager(mContext)
            }

            btnLogout.setOnClickListener {
                 Intent(mContext, AuthActivity::class.java).apply {
                     flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 }.also {
                        startActivity(it)
                 }
                requireActivity().finish()
            }

            pendingUsersAdapter.onAcceptClick = {
                 usersViewModel.approveUser(it,true)

            }
            pendingUsersAdapter.onRejectClick={

                usersViewModel.rejectUser(it)

            }


            lifecycleScope.launch {
                usersViewModel.usersResponse.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collectLatest {
                        when (it) {
                            is MyResponse.Loading -> {
                                pbLoading.isVisible = true
                                pendingUsersAdapter.submitList(emptyList())
                                tvNoUsers.isVisible = false
                            }
                            is MyResponse.Success -> {
                                pbLoading.isVisible = false
                                pendingUsersAdapter.submitList(it.data)
                                if (it.data.isEmpty()) {
                                    tvNoUsers.isVisible = true
                                }

                            }
                            is MyResponse.Failure -> {
                                pbLoading.isVisible = false
                                tvNoUsers.text = it.msg
                              tvNoUsers.isVisible = true
                            }

                            else -> {
                                pbLoading.isVisible = false
                            }
                        }
                    }
            }

        }
    }




}


