package com.services.provider.ui.pending

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.ActivityAddServiceBinding
import com.services.provider.databinding.ActivityPendingBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.SkilledService
import com.services.provider.domain.model.UserStatus
import com.services.provider.domain.model.serviceCategories
import com.services.provider.ui.admin.UsersViewModel
import com.services.provider.ui.auth.AuthActivity
import com.services.provider.ui.auth.signup.showToast
import com.services.provider.ui.dialogs.ProgressDialogUtil
import com.services.provider.ui.main.SkilledMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PendingActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivityPendingBinding

    private val usersViewModel: UsersViewModel by viewModels()

    @Inject
    lateinit var pref: MyPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            pref.currentUser = ""
            startActivity(Intent(mContext, AuthActivity::class.java))
           finish()
        }
        lifecycleScope.launch {
             val status=usersViewModel.getCurrentUserStatus()
                when (status) {
                    is MyResponse.Success -> {
                        binding.pbLoading.makeGone()
                        val mStatus= status.data
                        when (mStatus) {
                            UserStatus.PENDING -> {
                                binding.tvStatus.makeVisible()

                                binding.tvStatus.text = "Your account is pending for approval. It may take 24 hours."
                             }
                            UserStatus.APPROVED -> {
                                val intent =
                                    Intent(this@PendingActivity, SkilledMainActivity::class.java)
                                startActivity(intent)
                                finish()
                                      }
                            UserStatus.REJECTED -> {
                                binding.tvStatus.makeVisible()

                                binding.tvStatus.text = "Your account has been rejected"
                                    }
                            UserStatus.DISABLED -> {
                                binding.tvStatus.makeVisible()

                                binding.tvStatus.text = "Your account has been disabled"
                            }

                            UserStatus.DELETED -> {
                                binding.tvStatus.makeVisible()

                                binding.tvStatus.text = "Your account has been deleted"
                            }
                        }


                    }

                    is MyResponse.Failure ->{

                    }
                    MyResponse.Idle ->{

                    }
                    MyResponse.Loading ->{
                        binding.pbLoading.makeVisible()
                        binding.tvStatus.makeGone()

                    }
                }
        }

    }

    override fun onBackPress() {
        finish()
    }


}


