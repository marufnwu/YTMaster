package com.sikderithub.ytmaster.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sikderithub.ytmaster.Model.Login
import com.sikderithub.ytmaster.Model.Profile
import com.sikderithub.ytmaster.repo.YtRepo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.ScreenState

class LoginViewModel(val myApi: MyApi) : ViewModel() {
    private val ytRepo = YtRepo(myApi)
    private val _loginRes : MutableLiveData<ScreenState<Login>> = MutableLiveData()

    val loginRes : LiveData<ScreenState<Login>>
        get() = _loginRes


    fun login(gToken: String){
        _loginRes.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.login(gToken)
            if(res.isSuccessful && res.body()!=null){
                val r = res.body()!!
                if(!r.error){
                    _loginRes.postValue(ScreenState.Success(data = r))
                }else{
                    _loginRes.postValue(ScreenState.Error(data = null, r.msg))
                }
            }else{
                _loginRes.postValue(ScreenState.Error(data = null, res.message()))
            }
        }
    }
}