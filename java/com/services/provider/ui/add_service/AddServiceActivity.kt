package com.services.provider.ui.add_service

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.ActivityAddServiceBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.serviceCategories
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.dialogs.ProgressDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddServiceActivity : AppCompatActivity() {
    @Inject
    lateinit var binding: ActivityAddServiceBinding

    companion object {
        var skilledService: SkilledService = SkilledService()
    }

    private val addServiceViewModel: AddServiceViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val data: Intent? = it.data
                selectedImageUri = data?.data
                binding.ivService.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isIdEmpty = intent.getStringExtra("id").isNullOrEmpty()
        if (isIdEmpty) {
            skilledService = SkilledService()
        }

         with(binding) {
            ivServiceCamera.setOnClickListener {
                openGalleryForImage()
            }
             ivBack.setOnClickListener {
                 finish()
             }
            if (skilledService.id.isNotEmpty()) {
                btnDeleteService.makeVisible()
                etServiceTitle.setText(skilledService.title)
                etServiceDescription.setText(skilledService.description)
                etServicePrice.setText(skilledService.price)
                skilledService.serviceCategory = skilledService.serviceCategory
                binding.spinnerChooseCategory.setSelection(serviceCategories.indexOf(skilledService.serviceCategory))
                Glide.with(this@AddServiceActivity).load(skilledService.imageUrl).into(ivService)
            }
             else
            {
                btnDeleteService.makeGone()
            }

            lifecycleScope.launch {
                addServiceViewModel.addServiceResponse.collectLatest {
                    when (it) {
                        is MyResponse.Failure -> {
                            ProgressDialogUtil.dismissProgressDialog()
                            showToast(it.msg)
                        }

                        MyResponse.Loading -> {
                            ProgressDialogUtil.showProgressDialog(this@AddServiceActivity)
                        }

                        is MyResponse.Success -> {
                            ProgressDialogUtil.dismissProgressDialog()
                            showToast("Service added successfully")
                            finish()

                        }

                        null -> {

                        }

                        MyResponse.Idle -> {

                        }
                    }
                }
            }

            btnUploadService.setOnClickListener {
                val title = etServiceTitle.text.toString()
                val description = etServiceDescription.text.toString()
                val price = etServicePrice.text.toString()
                val serviceCategory = spinnerChooseCategory.selectedItem.toString()
                if (title.isEmpty()) {
                    showToast("Please enter title")
                } else if (description.isEmpty()) {
                    showToast("Please enter description")
                } else if (price.isEmpty()) {
                    showToast("Please enter price")
                } else if (selectedImageUri == null && skilledService.id.isEmpty()) {
                    showToast("Please select image")
                } else {
                    skilledService.apply {
                        this.title = title
                        this.description = description
                        this.price = price
                        this.imageUri = selectedImageUri
                        this.serviceCategory = serviceCategory
                    }
                    ProgressDialogUtil.showProgressDialog(this@AddServiceActivity)
                    addServiceViewModel.addService(skilledService)
                }

            }

             btnDeleteService.setOnClickListener {
                 addServiceViewModel.deleteService(skilledService)
                 showToast("Service Deleted")
                 finish()
             }

         }
    }

    private fun openGalleryForImage() {
        imageResultLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        })
    }


}


