package com.sikderithub.viewsgrow.utils

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.sikderithub.viewsgrow.repo.network.MyApi
import io.paperdb.Paper

class MyApp : Application() {
    val myApi by lazy {
        MyApi.invoke()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApp? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun isLogged() : Boolean{
            val token = LocalDB.getAccessToken()
            val userId = LocalDB.getUserId()

            if(token!=null && userId!=null && LocalDB.getProfile()!=null){
                return true
            }

            return false
        }

        fun logout(){
            LocalDB.removeLogin()
        }

        fun updateFcmToken(){
            try {
                if(isLogged()){

                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            Coroutines.main {
                                try {
                                    (applicationContext() as MyApp).myApi
                                        .updateFcmToken(token)
                                }catch (e:Exception){

                                }
                            }
                        }
                    })


                }
            }catch (e:Exception){

            }
        }


    }


    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        Firebase.messaging.subscribeToTopic("AllUsers")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }

            }




    }


}