package com.sikderithub.ytmaster.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.sikderithub.ytmaster.R
import com.sikderithub.ytmaster.databinding.ActivitySplashBinding
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.ui.login.LoginActivity
import com.sikderithub.ytmaster.ui.main.MainActivity
import com.sikderithub.ytmaster.ui.main.MainActivityViewModel
import com.sikderithub.ytmaster.ui.main.MainViewModelProvider
import com.sikderithub.ytmaster.ui.userdetails.UserDetailsActivity
import com.sikderithub.ytmaster.ui.userdetails.UserDetailsViewModel
import com.sikderithub.ytmaster.utils.*
import com.sikderithub.ytmaster.utils.MyExtensions.shortToast
import io.paperdb.Paper


class SplashActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashBinding
    lateinit var auth : FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    lateinit var loadingDialog: LoadingDialog
    val viewModel : SplashViewModel by lazy {
        ViewModelProvider(this, SplashViewModelProviders((application as MyApp).myApi))[SplashViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(applicationContext)
        loadingDialog = LoadingDialog(this)
        auth = FirebaseAuth.getInstance()

        initObserver()

    }

    private fun initObserver() {
        viewModel.isUserRegistered.observe(this){
            it?.let {
                when(it){
                    is ScreenState.Success->{
                        loadingDialog.hide()
                        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }

                    is ScreenState.Loading->{
                        loadingDialog.show()
                    }

                    is ScreenState.Error->{
                        loadingDialog.hide()
                        startActivity(Intent(this, UserDetailsActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //checkSigning()
        checkLogin()

//        if(auth.currentUser==null){
//            shortToast("null")
//            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//            finish()
//        }else{
//            auth.currentUser!!.getIdToken(true).addOnCompleteListener {
//                Log.d("IdToken", it.result.token!!)
//                Log.d("Id", auth.currentUser!!.uid)
//
//                Paper.book().write(Constant.SIGNING_ID_TOKEN,  it.result.token!!)
//                viewModel.isUserRegistered()
//            }
//
//        }
    }

    private fun checkLogin(){

        if(MyApp.isLogged()){
            //checkAuthorization
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }else{
            //go to main activity
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    private fun checkSigning() {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account==null){
            //not signed before
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }else{
            //already signed
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.GOOGLE_WEB_CLIENT))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            googleSignInClient.silentSignIn()
                .addOnCompleteListener {
                    handleSignIn(it)
                }
        }



    }

    private fun handleSignIn(it: Task<GoogleSignInAccount>) {
        if (it.isSuccessful){
            Log.d("SplashActivity", "Signed")
            //already signed
            Log.d("Token", it.result.idToken!!)
            Paper.book().write(Constant.SIGNING_ID_TOKEN, it.result.idToken!!)
            viewModel.isUserRegistered()
        }else{
            //not signed
            Log.d("SplashActivity", "Not Signed ${it.exception}")
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

}

class SplashViewModelProviders(val myApi: MyApi) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}