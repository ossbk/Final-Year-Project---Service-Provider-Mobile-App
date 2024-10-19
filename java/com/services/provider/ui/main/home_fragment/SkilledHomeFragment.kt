package com.services.provider.ui.main.home_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.FragmentSkilledHomeBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.serviceCategories
import com.services.provider.ui.add_service.AddServiceActivity
import com.services.provider.ui.auth.AuthActivity
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.main.MainServicesAdapter
import com.services.provider.ui.main.SkilledServiceViewModel
import com.services.provider.ui.main.category.CategoryActivity
import com.services.provider.ui.profile.ProfileActivity
import com.services.provider.ui.service_details.ServiceDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SkilledHomeFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentSkilledHomeBinding

    @Inject
    lateinit var pref: MyPref

    private lateinit var mainServicesAdapter: MainServicesAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val skilledServiceViewModel: SkilledServiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = pref.currentUser.toCurrentUser()

        binding.tvHomeTitle.text = "${currentUser?.firstName}  ${currentUser?.lastName}"


        with(binding) {

            rvServices.apply {
                mainServicesAdapter = MainServicesAdapter(pref)
                adapter = mainServicesAdapter
                layoutManager = LinearLayoutManager(mContext)
            }

            rvCategories.apply {
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                categoryAdapter = CategoryAdapter(pref)
                adapter = categoryAdapter
            }


            rvCategories.isVisible=pref.isCustomer()
            categoryAdapter.submitList(serviceCategories)
            categoryAdapter.onItemClick = {
                val intent = Intent(mContext, CategoryActivity::class.java)
                intent.putExtra("categoryName", it)
                startActivity(intent)
            }


            Glide.with(mContext.applicationContext).load(currentUser?.profilePhoto)
                .placeholder(R.drawable.profile_icon_empty).error(R.drawable.profile_icon_empty)
                .into(ivProfilePhoto)

            btnLogout.setOnClickListener {
                pref.currentUser = ""
                startActivity(Intent(mContext, AuthActivity::class.java))
                mContext.finish()
            }


            btnAddService.setOnClickListener {
                startActivity(Intent(mContext, AddServiceActivity::class.java))
            }
            btnAddService.isVisible = !pref.isCustomer()
            btnTryAgain.setOnClickListener {
                skilledServiceViewModel.getServices()
            }



            mainServicesAdapter.editClicked = {
                AddServiceActivity.skilledService = it
                val intent = Intent(mContext, AddServiceActivity::class.java)
                intent.putExtra("id", it.id)
                startActivity(intent)
            }

            mainServicesAdapter.onItemClick = {
                ServiceDetailsActivity.serviceDetails = it
                startActivity(Intent(mContext, ServiceDetailsActivity::class.java))

            }

            ivProfilePhoto.setOnClickListener {
                if (currentUser != null) {
                    ProfileActivity.userDetail = currentUser
                    Intent(mContext, ProfileActivity::class.java).apply {
                        putExtra("isMainUser", true)
                    }.also {
                        startActivity(it)
                    }
                }

            }


            lifecycleScope.launch {
                skilledServiceViewModel.getServicesResponse.flowWithLifecycle(
                    lifecycle,
                    Lifecycle.State.STARTED
                ).collectLatest {
                    when (it) {
                        is MyResponse.Failure -> {
                            pbMain.visibility = View.GONE
                            tvError.text = it.msg
                            mContext.showToast(it.msg)

                            clError.visibility = View.VISIBLE

                        }

                        MyResponse.Loading -> {
                            tvEmpty.makeGone()

                            clError.visibility = View.GONE
                            pbMain.visibility = View.VISIBLE
                            mainServicesAdapter.submitList(emptyList())
                        }

                        is MyResponse.Success -> {
                            pbMain.visibility = View.GONE
                            if (it.data.isEmpty()){
                                tvEmpty.makeVisible()
                            }
                            else {
                                tvEmpty.makeGone()
                                mainServicesAdapter.submitList(it.data)
                            }
                            clError.visibility = View.GONE

                        }

                        null -> {

                        }

                        MyResponse.Idle -> {

                        }
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        skilledServiceViewModel.getServices()
        Log.d("cvrr","resume profile ${pref.currentUser.toCurrentUser()}")
        Glide.with(mContext.applicationContext).load(pref.currentUser.toCurrentUser()?.profilePhoto)
            .placeholder(R.drawable.ic_launcher_background).error(R.drawable.profile_icon_empty)
            .into(binding.ivProfilePhoto)
    }


}


