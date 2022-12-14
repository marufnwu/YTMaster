package com.sikderithub.viewsgrow.ui.userdetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sikderithub.viewsgrow.databinding.ActivityUserDetailsBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.utils.*
import com.sikderithub.viewsgrow.utils.MyExtensions.shortToast
import io.paperdb.Paper

class UserDetailsActivity : AppCompatActivity() {
    private var acc: GoogleSignInAccount? = null
    lateinit var binding: ActivityUserDetailsBinding
    lateinit var loadingDialog: LoadingDialog
    val viewModel : UserDetailsViewModel by lazy {
        ViewModelProvider(this, UserDetailsViewModelProvider((application as MyApp).myApi))[UserDetailsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)


        initPhoneField()
        val email = Paper.book().read<String>(Constant.SIGNING_EMAIl)
        binding.email.editText?.setText(email)
        binding.email.isEnabled = false




        acc = GoogleSignIn.getLastSignedInAccount(this)

        initObserver()

        binding.btnSubmit.setOnClickListener {
            validateForm()
        }
    }

    private fun initPhoneField() {
        binding.cppView.registerCarrierNumberEditText(binding.edtPhone)
        binding.cppView.setNumberAutoFormattingEnabled(true)

        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.edtPhone.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.edtPhone.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.cppView.isValidFullNumber.let {
                    if(!it){
                        binding.edtPhone.error = "Number not valid"
                    }else{
                        binding.edtPhone.error = null
                    }
                }
            }

        })

    }

    private fun initObserver() {
        viewModel.signUpLiveData.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success->{
                        loadingDialog.hide()

                        CommonMethod.loginResponse(it.data, this)
                    }
                    is ScreenState.Loading->{
                        loadingDialog.show()
                    }

                    is ScreenState.Error->{
                        loadingDialog.hide()
                        it.message?.let {
                            shortToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun validateForm() {
        val fullname = binding.fullName.editText?.text.toString()
        val email = binding.email.editText?.text.toString()
        val city = binding.city.editText?.text.toString()
        val state = binding.state.editText?.text.toString()

        val chLink = binding.chanelLink.editText?.text.toString()


        val country = binding.cppCountry.selectedCountryName


        var phone :String? = null
        if(!binding.cppView.isValidFullNumber){
            shortToast("Phone number not valid")
            return
        }

        phone = binding.cppView.fullNumberWithPlus



        if(fullname.isNotEmpty() && email.isNotEmpty() && city.isNotEmpty() && country.isNotEmpty() && state.isNotEmpty()){
            var ch = ""
            if (!chLink.isEmpty()){
                if(CommonMethod.isYoutubeChanelLinkValid(chLink)!=null){
                    ch = chLink

                }else{
                    shortToast("Youtube channel link is not valid")
                    return
                }
            }
            viewModel.signUp(fullname, email, phone, city, state, country, ch, acc!!.photoUrl.toString(), acc!!.id)
        }else{
            shortToast("Some field must not empty")
        }
    }

    override fun onStart() {
        super.onStart()
        if(acc==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

class UserDetailsViewModelProvider(private val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailsViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}