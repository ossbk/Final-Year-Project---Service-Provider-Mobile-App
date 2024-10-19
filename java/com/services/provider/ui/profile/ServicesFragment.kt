package com.services.provider.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.FragmentServicesBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.add_service.AddServiceActivity
import com.services.provider.ui.main.MainServicesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServicesFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentServicesBinding

    val profileViewModel: ProfileViewModel by activityViewModels()

    @Inject
    lateinit var pref: MyPref
    lateinit var servicesAdapter: MainServicesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()



        with(binding) {
            rvServices.apply {
                layoutManager = LinearLayoutManager(mContext)
                servicesAdapter = MainServicesAdapter(pref)
                adapter = servicesAdapter
            }
            lifecycleScope.launch {
                profileViewModel.userProfileDetails.collectLatest { myResponse ->
                    when (myResponse) {
                        is MyResponse.Failure -> {
                            Log.d("cvrr","MyResponse failure =$myResponse")

                        }

                        MyResponse.Idle -> {

                        }

                        MyResponse.Loading -> {
                            tvEmpty.makeGone()
                            Log.d("cvrr","MyResponse loading =$myResponse")


                        }

                        is MyResponse.Success -> {
                            Log.d("cvrr","MyResponse =${myResponse.data}")
                            if (myResponse.data.services.isEmpty()){
                                tvEmpty.makeVisible()
                            }
                            else {
                                servicesAdapter.submitList(myResponse.data.services)
                                tvEmpty.makeGone()
                            }
                        }
                    }
                }
            }

            servicesAdapter.editClicked={
                AddServiceActivity.skilledService = it
                val intent = Intent(mContext, AddServiceActivity::class.java)
                intent.putExtra("id", it.id)
                startActivity(intent)
            }
        }

    }

    private fun handleClicks() {
        with(binding) {

        }
    }


}