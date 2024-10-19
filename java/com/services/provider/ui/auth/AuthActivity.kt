package com.services.provider.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.services.provider.R
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.databinding.ActivityAuthBinding
import com.services.provider.ui.pending.PendingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivityAuthBinding

    @Inject
    lateinit var pref: MyPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (pref.currentUser.toCurrentUser()?.id?.isNotEmpty() == true) {
            val intent =
                Intent(mContext, PendingActivity::class.java)
            startActivity(intent)
            finish()
        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        host.navController
    }

    override fun onBackPress() {
        finish()
    }


}