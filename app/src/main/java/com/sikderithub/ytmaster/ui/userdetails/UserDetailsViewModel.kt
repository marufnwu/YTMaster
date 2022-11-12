package com.sikderithub.ytmaster.ui.userdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.ytmaster.Model.Login
import com.sikderithub.ytmaster.Model.User
import com.sikderithub.ytmaster.repo.YtRepo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.ScreenState

class UserDetailsViewModel(private val  myApi: MyApi) : ViewModel()  {

    private val repo = YtRepo(myApi)


    val signUpLiveData: MutableLiveData<ScreenState<Login>> by lazy {
        MutableLiveData<ScreenState<Login>>()
    }

    fun signUp(
        fullname: String,
        email: String,
        phone: String,
        city: String,
        state: String,
        country: String,
        youtubeChanelLink: String,
        profilePic: String,
        googleId: String?
    ){
         Coroutines.main {
             signUpLiveData.postValue(ScreenState.Loading(null))
             val res = repo.signup(fullname, email ,phone , city, state , country ,youtubeChanelLink, profilePic, googleId)

             if(res.isSuccessful){
                 val userRes = res.body()

                 if(!userRes!!.error){
                     signUpLiveData.postValue(ScreenState.Success(userRes))
                 }else{
                     signUpLiveData.postValue(ScreenState.Error(null, userRes.msg))
                 }
             }else{
                 signUpLiveData.postValue(ScreenState.Error(null, res.message()))
             }
         }
    }
}