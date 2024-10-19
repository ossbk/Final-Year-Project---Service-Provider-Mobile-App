package com.services.provider.ui.main.chat_fragment

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.services.provider.base.BaseFragment
import com.services.provider.data.prefs.MyPref
import com.services.provider.data.repo.MessageRepositoryImpl
import com.services.provider.data.repo.toCurrentUser
import com.services.provider.data.utils.makeGone
import com.services.provider.data.utils.makeVisible
import com.services.provider.databinding.FragmentChatsBinding
import com.services.provider.domain.model.MyResponse
import com.services.provider.domain.model.Recipient
import com.services.provider.ui.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment:BaseFragment() {
    @Inject
    lateinit var binding:FragmentChatsBinding



    private var textView: TextView? = null
    private var locationManager: LocationManager? = null



    @Inject
    lateinit var prefHelper: MyPref


    private val chatViewModel:ChatViewModel by viewModels()
    lateinit var chatsAdapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvChats.apply {
                layoutManager = LinearLayoutManager(mContext)
                chatsAdapter = ChatsAdapter(prefHelper)
                adapter = chatsAdapter
                itemAnimator = null

            }


            //  checkLocationPermission()





            viewLifecycleOwner.lifecycleScope.launch {
repeatOnLifecycle(Lifecycle.State.RESUMED) {
    chatViewModel.allChats.collectLatest {
        when (it) {
            is MyResponse.Failure -> {
                pbChats.makeGone()

                Log.d("CHatsFragment", "onViewCreated: ${it.msg}")
            }

            is MyResponse.Loading -> {
                pbChats.makeVisible()
                Log.d("CHatsFragment", "Loading")

            }

            is MyResponse.Success -> {
                pbChats.makeGone()

                Log.d("CHatsFragment", "onViewCreated: ${it.data?.size}")
                chatsAdapter.submitList(it.data ?: arrayListOf())
            }

            MyResponse.Idle -> {

            }
        }
    }

}

            }

            chatsAdapter.onChatClick = {
                val currentUser=prefHelper.currentUser.toCurrentUser()!!.id
                val receiver: Recipient =it.recipients.filter { it.key!=currentUser }.values.firstOrNull()!!

                Intent(mContext, ChatActivity::class.java).apply {
                    putExtra("receiver", receiver)
                    startActivity(this)
                }
            }


        }
    }


}
