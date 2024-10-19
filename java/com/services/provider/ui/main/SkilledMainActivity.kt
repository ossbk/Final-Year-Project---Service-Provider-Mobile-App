package com.services.provider.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.services.provider.R
import com.services.provider.base.BaseActivity
import com.services.provider.data.prefs.MyPref
import com.services.provider.databinding.ActivitySkilledMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SkilledMainActivity : BaseActivity() {
    @Inject
    lateinit var binding: ActivitySkilledMainBinding

    @Inject
    lateinit var pref: MyPref

    private val skilledServiceViewModel: SkilledServiceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        with(binding) {


            val navHost = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment
            ) as NavHostFragment
            val navController = navHost.navController

            userBottomNav.setupWithNavController(navController)


        }
    }

    override fun onResume() {
        super.onResume()
        skilledServiceViewModel.getServices()
    }

    override fun onBackPress() {
        finish()
    }
}