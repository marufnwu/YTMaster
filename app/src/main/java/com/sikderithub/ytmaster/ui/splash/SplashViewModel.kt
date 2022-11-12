package com.sikderithub.ytmaster.ui.splash

import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.sikderithub.ytmaster.Model.Profile
import com.sikderithub.ytmaster.repo.YtRepo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.CommonMethod
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.ScreenState

class SplashViewModel(myApi: MyApi) : ViewModel(), LifecycleObserver {

    private val ytRepo = YtRepo(myApi)
    private val _isUserRegistered : MutableLiveData<ScreenState<Profile>> = MutableLiveData()

    val isUserRegistered : LiveData<ScreenState<Profile>>
        get() = _isUserRegistered

    fun isUserRegistered(){
        try{
            Coroutines.main {
                _isUserRegistered.postValue(ScreenState.Loading(null))
                val res = ytRepo.isUserRegistered()
                if(res.isSuccessful && res.body()!=null){
                    if(!res.body()!!.error){
                        _isUserRegistered.postValue(ScreenState.Success(res.body()!!.data!!))
                        CommonMethod.profile = res.body()!!.data
                    }else{
                        _isUserRegistered.postValue(ScreenState.Error(null, res.body()!!.msg))
                    }
                }else{
                    _isUserRegistered.postValue(ScreenState.Error(data = null, message = res.message()))
                }
            }
        }catch (e : Exception){
            _isUserRegistered.postValue(ScreenState.Error(null, e.message.toString()))
        }
    }
}