package com.sikderithub.viewsgrow.ui.splash

import androidx.lifecycle.*
import com.sikderithub.viewsgrow.Model.Login
import com.sikderithub.viewsgrow.Model.Profile
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.CommonMethod
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class SplashViewModel(myApi: MyApi) : ViewModel(), LifecycleObserver {

    private val ytRepo = YtRepo(myApi)
    private val _isUserRegistered : MutableLiveData<ScreenState<Profile>> = MutableLiveData()

    private val _auth : MutableLiveData<ScreenState<Login>> = MutableLiveData()

    val isUserRegistered : LiveData<ScreenState<Profile>>
        get() = _isUserRegistered

    val auth : LiveData<ScreenState<Login>>
        get()= _auth

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

    fun accessTokenAuth(){
        _auth.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.accessTokenAuth()
            if(res.isSuccessful && res.body()!=null){
                if(res.body()!!.error){
                    _auth.postValue(ScreenState.Error(message = res.body()!!.msg))
                }else{
                    _auth.postValue(ScreenState.Success(data = res.body()))
                }
            }else{
                _auth.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }
}