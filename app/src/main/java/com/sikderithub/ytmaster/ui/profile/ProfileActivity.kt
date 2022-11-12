package com.sikderithub.ytmaster.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.sikderithub.ytmaster.Model.Profile
import com.sikderithub.ytmaster.R
import com.sikderithub.ytmaster.databinding.ActivityProfileBinding
import com.sikderithub.ytmaster.ui.login.LoginActivity
import com.sikderithub.ytmaster.ui.userdetails.UserDetailsViewModel
import com.sikderithub.ytmaster.ui.userdetails.UserDetailsViewModelProvider
import com.sikderithub.ytmaster.utils.*
import com.sikderithub.ytmaster.utils.MyExtensions.setUrl
import com.sikderithub.ytmaster.utils.MyExtensions.shortToast

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    var otherProfile : Profile? = null

    val viewModel : ProfileViewModel by lazy {
        ViewModelProvider(this, ProfileViewModelProvider((application as MyApp).myApi))[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

         intent?.getSerializableExtra(Constant.OTHER_PROFILE)?.let {
             otherProfile = it as Profile
         }

        init()
        addObserver()


    }

    private fun addObserver() {
        viewModel.channelInfo.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success->{

                        binding.layoutChanelInfo.visibility  =View.VISIBLE

                        val chanelInfo = it.data
                        chanelInfo?.let {
                            if(it.items!=null && it.items!!.size>0){
                                val item = it.items!![0]!!
                                item.brandingSettings?.let {
                                    it.image?.let {
                                        it.bannerExternalUrl?.let {
                                            binding.imgChannelBanner.visibility = View.VISIBLE
                                            binding.imgChannelBanner.setUrl(it)
                                        }
                                    }
                                }

                                item.snippet?.let {
                                    it.title?.let {
                                        binding.txtChannelName.visibility = View.VISIBLE
                                        binding.txtChannelName.text = it
                                    }

                                    it.thumbnails?.let {
                                        it.high?.let {
                                            it?.url?.let {
                                                binding.imgChanelProfile.visibility = View.VISIBLE
                                                binding.imgChanelProfile.setUrl(it)
                                            }
                                        }
                                    }
                                }

                                item.statistics?.let {
                                    if(!it.hiddenSubscriberCount!!){
                                        binding.txtChannelCount.text = CommonMethod.valueToUnit(it.subscriberCount!!.toLong())+" Subscribers"
                                        binding.txtChannelViews.text = CommonMethod.valueToUnit(it.viewCount!!.toLong())+" Views"
                                    }
                                }


                            }
                        }
                    }
                    is ScreenState.Loading->{

                    }
                    is ScreenState.Error->{

                    }
                }
            }
        }
    }

    private fun init(){
        var profile :Profile?= null

        if(otherProfile!=null){
            //other profile
            profile = otherProfile
        }else{
            //my profile
            if(LocalDB.getProfile()==null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                profile = LocalDB.getProfile()
            }
        }

        profile?.let {
            profile.profilePic?.let {
                binding.imgProfile.setUrl(it)
            }

            profile.fullName?.let {
                binding.txtName.text = it
            }

            profile.email?.let {
                binding.txtEmail.text = it
            }

            profile.joinDate?.let {
                binding.txtRegDate.text = it
            }

            profile.chanelLink?.let {
                if(it.isNotEmpty()){
                    viewModel.getChanelInfo(it)
                }
            }
        }

    }
}