package com.sikderithub.viewsgrow.ui.main

import android.app.Activity
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.maruf.jdialog.JDialog
import com.sikderithub.viewsgrow.Model.HighLightedChannel
import com.sikderithub.viewsgrow.Model.Link
import com.sikderithub.viewsgrow.Model.Profile
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.adapter.ChannelsAdapter
import com.sikderithub.viewsgrow.adapter.LinkListAdapter
import com.sikderithub.viewsgrow.adapter.UserListAdapter
import com.sikderithub.viewsgrow.adapter.ViewType
import com.sikderithub.viewsgrow.databinding.ActivityMainBinding
import com.sikderithub.viewsgrow.databinding.LayoutNavBottomSheetBinding
import com.sikderithub.viewsgrow.layout_manager.SmoothScrollingLayoutManager
import com.sikderithub.viewsgrow.ui.all_link.AllLinkActivity
import com.sikderithub.viewsgrow.ui.channel_list.ChannelListActivity
import com.sikderithub.viewsgrow.ui.create_subdomain.SubdomainCreateActivity
import com.sikderithub.viewsgrow.ui.generate_link.GenerateLinkActivity
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.ui.profile.ProfileActivity
import com.sikderithub.viewsgrow.ui.special_link.DomainCreateActivity
import com.sikderithub.viewsgrow.utils.*
import com.sikderithub.viewsgrow.utils.MyExtensions.addBanner
import com.sikderithub.viewsgrow.utils.MyExtensions.setUrl


class MainActivity : AppCompatActivity(), View.OnClickListener {
    var isNavBottomSheetShowing = false
    lateinit var bottomSheet: BottomSheetDialog
    private var acc: GoogleSignInAccount? = null
    lateinit var binding: ActivityMainBinding
    lateinit var linkAdapter : LinkListAdapter
    lateinit var thumbLinkAdapter : LinkListAdapter
    lateinit var userAdapter : UserListAdapter
    lateinit var channelAdapter : ChannelsAdapter
    var linkList = mutableListOf<Link>()
    var thumbLinkList = mutableListOf<Link>()
    var userList = mutableListOf<Profile>()
    var channelList = mutableListOf<HighLightedChannel>()
    lateinit var mainBottomSheet: MainBottomSheet

    val viewModel : MainActivityViewModel by lazy {
        ViewModelProvider(this, MainViewModelProvider((application as MyApp).myApi))[MainActivityViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainBottomSheet = MainBottomSheet.newInstance()

        acc = GoogleSignIn.getLastSignedInAccount(this)

        linkAdapter = LinkListAdapter(this, linkList,LinkListAdapter.ViewType.LINK)
        thumbLinkAdapter = LinkListAdapter(this, thumbLinkList ,LinkListAdapter.ViewType.THUMBNAIL)

        userAdapter = UserListAdapter(this, userList, ViewType.ROUND)
        channelAdapter = ChannelsAdapter(this, channelList, ChannelsAdapter.ViewType.ROUND)

        if (CommonMethod.haveInternet(getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)){
            initViews()
            addObServer()
            initClickListener()
        }else{
            JDialog.make(this)
                .setBodyText("No Internet connection. Please enable Mobile Data Or Wifi")
                .setIconType(JDialog.IconType.WARNING)
                .setPositiveButton("Retry"){
                    it.hideDialog()
                    startActivity(intent)

                }
                .build()
                .showDialog()
        }

    }

    private fun initViews() {
        initBottomSheet()
        val layoutManager  = SmoothScrollingLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyLink.layoutManager = layoutManager
        binding.recyLink.setHasFixedSize(true)
        //binding.recyLink.adapter = linkAdapter

        binding.recyLink.adapter = thumbLinkAdapter


        val speedScroll = 3000L
        val handler = Handler()
        val movePixel = layoutManager.getChildAt(layoutManager.findFirstVisibleItemPosition())?.height

        val runnable: Runnable = object : Runnable {
            var reverse = false
            var current = 1

            override fun run() {
                val currPos = layoutManager.findFirstVisibleItemPosition()
                val lastItem = layoutManager.findLastVisibleItemPosition()


                Log.d("CurrPos", currPos.toString())
                val totalItem = thumbLinkAdapter.itemCount -1

                if(current==totalItem){
                    //last item visible
                    reverse = true
                }else if (current==0){
                    //have next item
                    reverse = false
                }




                if(reverse){

                    binding.recyLink.smoothScrollToPosition(current--)


                }else{
                    Log.d("CurrentVisibleItem", layoutManager.findFirstCompletelyVisibleItemPosition().toString())
                    binding.recyLink.smoothScrollToPosition(current++)
                }

                handler.postDelayed(this, speedScroll)
            }
        }

        //handler.postDelayed(runnable, speedScroll)


        binding.recyUsers.layoutManager  = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyUsers.setHasFixedSize(true)
        //binding.recyUsers.adapter = userAdapter


        binding.recyUsers.layoutManager  = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyUsers.setHasFixedSize(true)
        binding.recyUsers.adapter = channelAdapter


        if(MyApp.isLogged()){
            LocalDB.getProfile()?.let {
                it.profilePic?.let {
                    binding.imgProfile.setUrl(it)
                }
            }

        }

        binding.imgProfile.setOnClickListener {
            if(MyApp.isLogged()){
                startActivity(Intent(this, ProfileActivity::class.java))
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }


        binding.recentLinkSeeAll.setOnClickListener {
            startActivity(Intent(this, AllLinkActivity::class.java))
        }

        binding.channelsSeeAll.setOnClickListener {
            startActivity(Intent(this, ChannelListActivity::class.java))
        }

        binding.imgDrawer.setOnClickListener {
            if(supportFragmentManager.findFragmentByTag("MainBottomSheet")==null){
                mainBottomSheet.show(supportFragmentManager, "MainBottomSheet")
            }
        }
    }

    private fun initClickListener() {
        binding.btnGen.setOnClickListener(this)
        binding.layoutCreateNewDomain.setOnClickListener(this)
        binding.layoutCreateNewSubDomain.setOnClickListener(this)
    }

    private fun addObServer() {

        viewModel.recentLinks.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success ->{
                        binding.recyLink.visibility = View.VISIBLE
                        binding.layoutProgress.visibility = View.GONE
                        binding.txtLinkError.visibility = View.GONE


                        it.data?.let {
                            it.maindata?.let {
                                linkList.clear()
                                linkList.addAll(it)
                                linkAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    is ScreenState.Loading ->{
                        binding.recyLink.visibility = View.GONE
                        binding.layoutProgress.visibility = View.VISIBLE
                        binding.txtLinkError.visibility = View.GONE


                    } is ScreenState.Error ->{
                        binding.recyLink.visibility = View.GONE
                        binding.layoutProgress.visibility = View.GONE
                        binding.txtLinkError.visibility = View.VISIBLE
                        binding.txtLinkError.text = it.message
                    }
                }
            }
        }


        viewModel.thumbLinks.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success ->{
                        binding.recyLink.visibility = View.VISIBLE
                        binding.layoutProgress.visibility = View.GONE
                        binding.txtLinkError.visibility = View.GONE


                        it.data?.let {
                            it.maindata?.let {
                                thumbLinkList.clear()
                                thumbLinkList.addAll(it)
                                thumbLinkAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    is ScreenState.Loading ->{
                        binding.recyLink.visibility = View.GONE
                        binding.layoutProgress.visibility = View.VISIBLE
                        binding.txtLinkError.visibility = View.GONE


                    } is ScreenState.Error ->{
                        binding.recyLink.visibility = View.GONE
                        binding.layoutProgress.visibility = View.GONE
                        binding.txtLinkError.visibility = View.VISIBLE
                        binding.txtLinkError.text = it.message
                    }
                }
            }
        }

        viewModel.userList.observe(this){
            when(it){
                is ScreenState.Success ->{
                    binding.layoutUserProgress.visibility = View.GONE
                    binding.recyUsers.visibility = View.VISIBLE

                    it.data?.let {
                        userList.addAll(it)
                        userAdapter.notifyDataSetChanged()
                    }


                }
                is ScreenState.Loading ->{
                    binding.layoutUserProgress.visibility = View.VISIBLE
                    binding.recyUsers.visibility = View.GONE
                } is ScreenState.Error ->{
                    binding.layoutUserProgress.visibility = View.GONE
                    binding.recyUsers.visibility = View.GONE
                }
            }
        }
        viewModel.highLightedChannels.observe(this){
            when(it){
                is ScreenState.Success ->{
                    binding.layoutUserProgress.visibility = View.GONE
                    binding.recyUsers.visibility = View.VISIBLE

                    it.data?.let {
                        it.maindata?.let {
                            channelList.addAll(it)
                            channelAdapter.notifyDataSetChanged()
                        }


                    }


                }
                is ScreenState.Loading ->{
                    binding.layoutUserProgress.visibility = View.VISIBLE
                    binding.recyUsers.visibility = View.GONE
                } is ScreenState.Error ->{
                    binding.layoutUserProgress.visibility = View.GONE
                    binding.recyUsers.visibility = View.GONE
                }
            }
        }

        viewModel.homePage.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success ->{
                       it.data?.let {
                           if(!it.mainBanner.error){
                               binding.shimmer.stopShimmer()
                               binding.imgMainBanner.addBanner(it.mainBanner)
                           }
                       }
                    }
                    is ScreenState.Loading ->{

                    } is ScreenState.Error ->{

                    }
                }
            }

        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when(it.id){
                R.id.btnGen-> {
                    generateLink()
                } R.id.layoutCreateNewDomain-> {
                    startActivity(Intent(this, DomainCreateActivity::class.java))
                } R.id.layoutCreateNewSubDomain-> {
                    startActivity(Intent(this, SubdomainCreateActivity::class.java))
                }
                else -> {}
            }
        }
    }

    val startLinkGenPageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.let {
                val isLinkAdded = it.getBooleanExtra(Constant.IS_LINK_ADDEDD, false)
                if(isLinkAdded){
                    //viewModel.getRecentLinks()
                }
            }
        }
    }


    private fun generateLink() {
        val link = binding.edtLink.text.toString()
        if(link.isNotEmpty()){

            if(CommonMethod.isLinkPermitted(link)){
                val intent = Intent(this, GenerateLinkActivity::class.java)
                intent.putExtra(Constant.LINK, link)
                startLinkGenPageForResult.launch(intent)
            }else{
                binding.edtLink.error = getString(R.string.yt_link_invalid)
            }
        }else{
            binding.edtLink.error = getString(R.string.link_empty)
        }
    }

    private fun initBottomSheet(){
        bottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheet.behavior.isHideable = true
        bottomSheet.behavior.skipCollapsed = true
        bottomSheet.dismissWithAnimation = true
        val navBottomSheetBinding = LayoutNavBottomSheetBinding.inflate(layoutInflater)
        bottomSheet.setContentView(R.layout.layout_nav_bottom_sheet)

        val loginBtn = bottomSheet.findViewById<Button>(R.id.btnLogin)


        bottomSheet.setOnShowListener {
            isNavBottomSheetShowing = true
        }

        bottomSheet.setOnDismissListener {
            isNavBottomSheetShowing = false
        }

        if(MyApp.isLogged()){

        }

    }


}