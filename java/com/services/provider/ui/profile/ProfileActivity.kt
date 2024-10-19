package com.services.provider.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.services.provider.R
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.ActivityProfileBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.User
import com.services.provider.ui.auth.signup.isCustomer
import com.services.provider.ui.auth.signup.isSameUser
import com.services.provider.ui.service_details.roundOfToDigits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    val tabsAdminList = arrayOf(
        "About",
        "Gigs",
        "Reviews"
    )
    val tabsCusList = arrayOf(
        "About",
        "Reviews"
    )


    @Inject
    lateinit var binding: ActivityProfileBinding
    val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var pref: MyPref

    companion object {
        var userDetail: User = User()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tabsList = if (userDetail.isCustomer()) tabsCusList else tabsAdminList
        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }

            val isMainUser = intent.getBooleanExtra("isMainUser", false)
            if (isMainUser) {
                ivEdit.makeVisible()
            }
            ivEdit.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, UpdateProfieActivity::class.java))
            }


            profileViewModel.getAllServices(userDetail)
            lifecycleScope.launch {
                profileViewModel.userProfileDetails.collectLatest { myResponse ->
                    when (myResponse) {
                        is MyResponse.Failure -> {
                            pbLoading.makeGone()
                            clError.makeVisible()
                            tvError.text = myResponse.msg
                        }

                        MyResponse.Idle -> {
                            pbLoading.makeVisible()
                            clError.makeGone()
                        }

                        MyResponse.Loading -> {
                            pbLoading.makeVisible()
                            clError.makeGone()
                            clDetails.makeGone()

                        }

                        is MyResponse.Success -> {
                            pbLoading.makeGone()
                            clError.makeGone()
                            clDetails.makeVisible()
                            tvProfileTitle.text = myResponse.data.user.getName()
                            if (!pref.isSameUser(userDetail.id)) {
                                Glide.with(this@ProfileActivity)
                                    .load(myResponse.data.user.profilePhoto)
                                    .placeholder(R.drawable.profile_icon_empty)
                                    .error(R.drawable.profile_icon_empty)
                                    .into(ivProfile)
                            }

                            val avgRating = myResponse.data.ratings.map {
                                it.rating
                            }.average().roundOfToDigits(2)
                            tvNoOfPeopleRated.text =
                                "(${myResponse.data.ratings.size.toString()})"
                            tvRating.text = avgRating.toString()

                        }
                    }
                }
            }


            val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, tabsList)
            viewPagerMain.adapter = adapter

            TabLayoutMediator(tabLayout, viewPagerMain) { tab, position ->

                tab.text = tabsList[position]

            }.attach()

        }

    }

    override fun onResume() {
        super.onResume()
        if (pref.isSameUser(userDetail.id)){
            Glide.with(this@ProfileActivity)
                .load(pref.currentUser.toCurrentUser()?.profilePhoto)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.profile_icon_empty)
                .into(binding.ivProfile)
        }
        profileViewModel.getAllServices(userDetail)

    }


}


private const val NUM_TABS = 3

public class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val tabsList: Array<String>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabsList.size
    }

    override fun createFragment(position: Int): Fragment {
        if (tabsList.size == 3) {
            when (position) {
                0 -> return UserDetailsFragment()
                1 -> return ServicesFragment()
                2 -> return RatingFragment()
            }
        } else {
            when (position) {
                0 -> return UserDetailsFragment()
                1 -> return RatingFragment()
            }

        }
        return UserDetailsFragment()
    }

}