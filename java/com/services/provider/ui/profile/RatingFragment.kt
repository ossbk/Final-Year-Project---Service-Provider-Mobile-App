package com.services.provider.ui.profile

import android.os.Bundle
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
import com.services.provider.databinding.FragmentRatingBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.ui.service_details.RatingAdapter
import com.services.provider.ui.service_details.roundOfToDigits
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RatingFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentRatingBinding

    val profileViewModel: ProfileViewModel by activityViewModels()

    @Inject
    lateinit var pref: MyPref
    lateinit var ratingsAdapte: RatingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClicks()



        with(binding) {
            rvRatings.apply {
                layoutManager = LinearLayoutManager(mContext)
                ratingsAdapte = RatingAdapter(pref)
                adapter = ratingsAdapte
            }
            lifecycleScope.launch {
                profileViewModel.userProfileDetails.collectLatest { myResponse ->
                    when (myResponse) {
                        is MyResponse.Failure -> {
                        }

                        MyResponse.Idle -> {

                        }

                        MyResponse.Loading -> {
                            tvEmpty.makeGone()

                        }

                        is MyResponse.Success -> {
                            val avgRating = myResponse.data.ratings.map {
                                it.rating
                            }.average().roundOfToDigits(2)
                            tvNoOfPeopleRated.text =
                                "(${myResponse.data.ratings.size.toString()})"
                            tvRating.text = avgRating.toString()
                            if (myResponse.data.ratings.isEmpty()){
                                tvEmpty.makeVisible()
                            }
                            else{
                                tvEmpty.makeGone()
                                ratingsAdapte.submitList(myResponse.data.ratings)

                            }
                        }
                    }
                }
            }
        }

    }

    private fun handleClicks() {
        with(binding) {

        }
    }


}