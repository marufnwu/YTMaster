package com.sikderithub.viewsgrow.ui.profile

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.nikartm.button.FitButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sikderithub.viewsgrow.Model.Link
import com.sikderithub.viewsgrow.Model.Profile
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.adapter.LinkListAdapter
import com.sikderithub.viewsgrow.databinding.ActivityProfileBinding
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.ui.my_link.MyLinkActivity
import com.sikderithub.viewsgrow.utils.*
import com.sikderithub.viewsgrow.utils.MyExtensions.setUrl
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast

class ProfileActivity : AppCompatActivity() {

    lateinit var linkAdapter: LinkListAdapter
    lateinit var binding: ActivityProfileBinding
    var otherProfile : Profile? = null
    var linkList = mutableListOf<Link>()


    val viewModel : ProfileViewModel by lazy {
        ViewModelProvider(this, ProfileViewModelProvider((application as MyApp).myApi))[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        linkAdapter = LinkListAdapter(this, linkList, LinkListAdapter.ViewType.LINK)


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


                        val chanelInfo = it.data
                        chanelInfo?.let {

                            if(it.items!=null && it.items!!.size>0){
                                binding.layoutChanelInfo.visibility  =View.VISIBLE

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

        viewModel.myLinks.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success ->{

                        binding.recyLink.visibility = View.VISIBLE
                        binding.layoutProgress.visibility = View.GONE
                        binding.txtLinkError.visibility = View.GONE


                        it.data?.let {

                            it.maindata?.let {
                                Log.d("ItemSize", it.size.toString())
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
    }

    private fun init(){
        addProfile()

        binding.recyLink.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyLink.setHasFixedSize(true)
        binding.recyLink.adapter = linkAdapter

        binding.recentLinkSeeAll.setOnClickListener {
            startActivity(Intent(this, MyLinkActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            MyApp.logout()
            finish()
        }

        binding.btnAddChannel.setOnClickListener {
            showAddChannelDialog()
        }

    }

    private fun addProfile() {
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
                }else{
                    binding.layoutChNotFound.visibility= View.VISIBLE
                }
            }
        }
    }

    private fun showAddChannelDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.simple_edittext_dialog)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = dialog.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val txtTitle = dialog.findViewById<TextView>(R.id.txtDialogTittle)
        txtTitle.setText("Add Channel")

        val linkInput = dialog.findViewById<TextInputEditText>(R.id.subdomainTxtInput)
        val linkInputLayout = dialog.findViewById<TextInputLayout>(R.id.edtSubdomain)
        val positive = dialog.findViewById<FitButton>(R.id.btnSubmit)
        val negative = dialog.findViewById<TextView>(R.id.txtCancel)


        linkInputLayout.hint = "Enter youtube channel url"

        positive.setText("Add")
        positive.setOnClickListener {

            val channel = linkInput.text.toString()

            if(channel.isEmpty()){
                shortToast("Channel name must not be empty")
                return@setOnClickListener
            }

            if(CommonMethod.isYoutubeChanelLinkValid(channel)!=null){
                val shortName = CommonMethod.isYoutubeChanelLinkValid(channel)
                Coroutines.main {
                    try {
                        val res = (application as MyApp)
                            .myApi
                            .updateChannel(shortName)

                        if(res.isSuccessful && res.body()!=null){
                            val body = res.body()!!

                            shortToast(body.msg)

                            if(!body.error){
                                dialog.dismiss()
                            }
                        }

                    }catch (e:Exception){
                        shortToast(e.message!!)
                    }
                }
            }else{
                shortToast("Youtube channel link not valid")
            }



        }

        negative.setOnClickListener{
            dialog.dismiss()

        }


        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        if(!MyApp.isLogged()){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}