package com.sikderithub.viewsgrow.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sikderithub.viewsgrow.R
import com.sikderithub.viewsgrow.databinding.ActivityLoginBinding
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.ui.splash.SplashActivity
import com.sikderithub.viewsgrow.utils.*
import io.paperdb.Paper


class LoginActivity : AppCompatActivity() {
    var account: GoogleSignInAccount?  = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    lateinit var binding : ActivityLoginBinding
    lateinit var auth : FirebaseAuth
    lateinit var loadingDialog : LoadingDialog

    companion object{
        private const val RC_SIGN_IN: Int = 1000001
    }

    val viewModel : LoginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelProviders((application as MyApp).myApi))[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        initObserver()
        FirebaseApp.initializeApp(applicationContext)


        auth = Firebase.auth
         gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.GOOGLE_WEB_CLIENT))
             .requestEmail()
             .requestProfile()
            .build()

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLogin.setOnClickListener {
            startSignIn()
        }

    }

    private fun initObserver() {
        viewModel.loginRes.observe(this){
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

                    }
                }
            }
        }
    }

    private fun startSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        account = GoogleSignIn.getLastSignedInAccount(this)
        //checkAccount(account)

        if(MyApp.isLogged()){
            startActivity(Intent(this, SplashActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                //firebaseAuthWithGoogle(account.idToken!!)
                handleSignInResult(task)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("SignInActivity", "Google sign in failed", e)
            }
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            it.getIdToken(true).addOnCompleteListener {
                Log.d("SigniInActivity", it.result.token!!)
            }

        }
    }
    // [END auth_with_google]

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            //checkAccount(account)
            login(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("LoginActivity", "signInResult:failed code=" + e.statusCode)
            login(null)
        }
    }

    private fun checkAccount(account: GoogleSignInAccount?) {
        if(account!=null){
            account.idToken?.let {
                Paper.book().write(Constant.SIGNING_ID_TOKEN, it)
            }

            startActivity(Intent(this, SplashActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    private fun login(account: GoogleSignInAccount?){
        if(account!=null){
            account.idToken?.let {
                Paper.book().write(Constant.SIGNING_ID_TOKEN, it)
                viewModel.login(it)
            }

        }
    }

}

class LoginViewModelProviders(val myApi: MyApi) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}