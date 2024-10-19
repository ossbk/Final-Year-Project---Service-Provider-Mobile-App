package com.services.provider.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.FragmentUserDetialsBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.auth.signup.isSameUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailsFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentUserDetialsBinding

    val profileViewModel: ProfileViewModel by activityViewModels()

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

        with(binding) {
            lifecycleScope.launch {
                profileViewModel.userProfileDetails.collectLatest { myResponse ->
                    when (myResponse) {
                        is MyResponse.Failure -> {
                        }

                        MyResponse.Idle -> {

                        }

                        MyResponse.Loading -> {
pbLoading.makeVisible()
                            clUserInfo.makeGone()

                        }

                        is MyResponse.Success -> {
                            pbLoading.makeGone()
                            val user=myResponse.data.user
                            tvUserCountry.text = user.countryName.ifEmpty {  "Not Added"}
                            tvUserInfo.text = user.userDetails.ifEmpty { "Not Added" }

                            tvQualification.text = user.qualification.ifEmpty { "Not Added" }

                            clUserInfo.makeVisible()
                        }
                    }
                }
            }
        }


    }

    private fun handleClicks() {

    }


    override fun onResume() {
        super.onResume()
        with(binding) {
            if (pref.isSameUser(userId = ProfileActivity.Companion.userDetail.id)){
                pbLoading.makeGone()
                val user=pref.currentUser.toCurrentUser()
                tvUserCountry.text = user?.countryName?.ifEmpty {  "Not Added"}
                tvUserInfo.text = user?.userDetails?.ifEmpty { "Not Added" }

                tvQualification.text = user?.qualification?.ifEmpty { "Not Added" }

                clUserInfo.makeVisible()
            }
        }
    }


}