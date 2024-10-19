package com.services.provider.ui.main.category

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.ActivityCategoryBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.add_service.AddServiceActivity
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.main.MainServicesAdapter
import com.services.provider.ui.main.search.SearchServicesViewModel
import com.services.provider.ui.service_details.ServiceDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryActivity : BaseActivity() {

    @Inject
    lateinit var binding: ActivityCategoryBinding
    val searchViewModel: SearchServicesViewModel by viewModels()
    private lateinit var mainServicesAdapter: MainServicesAdapter

    @Inject
    lateinit var prefRepository: MyPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val categoryName = intent.getStringExtra("categoryName")
        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
            tvTitle.setText(categoryName)
            searchViewModel.searchServices(categoryName.toString())

            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                mainServicesAdapter = MainServicesAdapter(prefRepository)
                adapter = mainServicesAdapter
            }

            lifecycleScope.launch {
                searchViewModel.searchResult.collectLatest {
                    when (it) {
                        is MyResponse.Failure -> {
                            pbLoading.visibility = View.GONE

                            Toast.makeText(
                                this@CategoryActivity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            )
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

    override fun onBackPress() {
        finish()
    }
}