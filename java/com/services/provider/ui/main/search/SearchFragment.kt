package com.services.provider.ui.main.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.FragmentSearchBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.add_service.AddServiceActivity
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.main.MainServicesAdapter
import com.services.provider.ui.service_details.ServiceDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentSearchBinding

    private val searchServicesViewModel: SearchServicesViewModel by viewModels()
    private lateinit var mainServicesAdapter: MainServicesAdapter

    @Inject
    lateinit var prefRepository: MyPref
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchServicesViewModel.searchServices(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })


            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                mainServicesAdapter = MainServicesAdapter(prefRepository)
                adapter = mainServicesAdapter
            }
            lifecycleScope.launch {
                searchServicesViewModel.searchResult.collectLatest {
                    when (it) {
                        is MyResponse.Failure -> {
                            pbLoading.visibility = View.GONE

                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                        MyResponse.Loading -> {
                            mainServicesAdapter.submitList(emptyList())
                            pbLoading.visibility = View.VISIBLE
                        }

                        is MyResponse.Success -> {
                            pbLoading.visibility = View.GONE
                            mainServicesAdapter.submitList(it.data)
                            tvNoServices.isVisible = it.data.isEmpty()
                        }

                        MyResponse.Idle -> {
                            pbLoading.visibility = View.GONE

                        }
                    }

                }
            }


            mainServicesAdapter.onItemClick = {
                if (prefRepository.isCustomer()) {
                    ServiceDetailsActivity.serviceDetails = it
                    startActivity(Intent(mContext, ServiceDetailsActivity::class.java))
                } else {
                    AddServiceActivity.skilledService = it
                    val intent = Intent(mContext, AddServiceActivity::class.java)
                    intent.putExtra("id", it.id)
                    startActivity(intent)
                }
            }


            mainServicesAdapter.editClicked = {
                AddServiceActivity.skilledService = it
                val intent = Intent(mContext, AddServiceActivity::class.java)
                intent.putExtra("id", it.id)
                startActivity(intent)
            }

        }
    }
}