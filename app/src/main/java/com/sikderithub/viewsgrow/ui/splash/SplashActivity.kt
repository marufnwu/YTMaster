package com.sikderithub.viewsgrow.ui.splash

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.ActivitySplashBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.login.LoginActivity
import com.sikderithub.viewsgrow.ui.main.MainActivity
import com.sikderithub.viewsgrow.ui.userdetails.UserDetailsActivity
import com.sikderithub.viewsgrow.utils.*
import io.paperdb.Paper
import kotlin.system.exitProcess


class SplashActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 10001
    lateinit var binding : ActivitySplashBinding
    lateinit var auth : FirebaseAuth
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    lateinit var loadingDialog: LoadingDialog
    private var appUpdate : AppUpdateManager? = null


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
        appUpdate = AppUpdateManagerFactory.create(this)

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

        viewModel.auth.observe(this){
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
                        MyApp.logout()
                        goToMainActivity()
                    }
                }
            }
        }
    }

    private fun checkUpdate(){
        Log.d("UpdateChecker", "Inside check update")
        appUpdate?.appUpdateInfo?.addOnSuccessListener{ updateInfo->

            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                Log.d("UpdateChecker", "Update Available version"+updateInfo.availableVersionCode())

                try{
                    appUpdate?.startUpdateFlowForResult(updateInfo,
                        AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
                }catch (e : IntentSender.SendIntentException){
                    e.printStackTrace()
                    checkLogin()
                }
            }else{
                Log.d("UpdateChecker", "App up to date")

                checkLogin()
            }

        }!!.addOnFailureListener {
            Log.d("UpdateChecker", it.message!!)

            checkLogin()
        }
    }

    override fun onStart() {
        super.onStart()

        checkPolicy()

        //checkUpdate()
    }

    private fun checkPolicy() {
        val isRead = LocalDB.getPolicyRead()
        if(isRead){
            checkUpdate()
        }else{
            shoPolicyDialog()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun shoPolicyDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_webview)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = dialog.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val title = dialog.findViewById<TextView>(R.id.txtTitle)
        val webView = dialog.findViewById<WebView>(R.id.webView)

        val btnAccept = dialog.findViewById<Button>(R.id.btnAccept)
        val btnDecline = dialog.findViewById<Button>(R.id.btnDecline)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(getString(R.string.privacy_url))

        title.setText("Privacy Policy")

        btnDecline.setOnClickListener {
            GenericDialog.make(this)
                .setCancelable(false)
                .setIconType(GenericDialog.IconType.WARNING)
                .setBodyText("For using this app you must agree to our Privacy Policy.")
                .setPositiveButton("Re-Check"){
                    it.hideDialog()
                }.setNegativeButton("Exit"){
                    it.hideDialog()
                    dialog.hide()

                    finish()
                    exitProcess(0)
                }.build().showDialog()
        }

        btnAccept.setOnClickListener {
            dialog.hide()
            LocalDB.setPolicyRead(true)
            checkUpdate()
        }

        dialog.show()
    }

    private fun checkLogin(){

        if(MyApp.isLogged()){
            //checkAuthorization
            if(CommonMethod.haveInternet(this)){
                viewModel.accessTokenAuth()
            }else{
                GenericDialog.make(this)
                    .setCancelable(false)
                    .setIconType(GenericDialog.IconType.WARNING)
                    .setBodyText(getString(R.string.no_internet))
                    .setPositiveButton("Retry"){
                        it.hideDialog()
                        checkLogin()
                    }.build()
                    .showDialog()
            }
        }else{
            //go to main activity
           goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
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

    class MyWebViewClient : WebViewClient() {
        
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