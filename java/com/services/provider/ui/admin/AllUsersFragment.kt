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
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.FragmentAllUsersBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.UserStatus
import com.services.provider.ui.auth.AuthActivity
import com.services.provider.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AllUsersFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentAllUsersBinding

    @Inject
    lateinit var pref: MyPref

    private lateinit var allUsersAdapter: AllUsersAdapter
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
            usersViewModel.getActiveUsers()

            rvUsers.apply {
                allUsersAdapter = AllUsersAdapter()
                adapter = allUsersAdapter
                layoutManager = LinearLayoutManager(mContext)
            }

            allUsersAdapter.onDeleteClick = {
                 usersViewModel.deleteUser(it)
            }
            allUsersAdapter.onDisableClick={
                if (it.status == UserStatus.DISABLED) {
                    it.status = UserStatus.APPROVED
                    usersViewModel.approveUser(it)
                }
                else {
                    it.status = UserStatus.DISABLED
                    usersViewModel.disableUser(it)
                }
                 allUsersAdapter.notifyDataSetChanged()

            }

            allUsersAdapter.onItemClicked={
val intent = Intent(mContext, ProfileActivity::class.java)
                ProfileActivity.userDetail = it
                 startActivity(intent)

            }


            btnLogout.setOnClickListener {
                Intent(mContext, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }.also {
                    startActivity(it)
                }
                requireActivity().finish()
            }

            lifecycleScope.launch {
                usersViewModel.usersResponse.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collectLatest {
                        when (it) {
                            is MyResponse.Loading -> {
                                pbLoading.isVisible = true
                            }
                            is MyResponse.Success -> {
                                pbLoading.isVisible = false
                                allUsersAdapter.submitList(it.data)
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


