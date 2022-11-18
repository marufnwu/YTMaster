package com.sikderithub.viewsgrow.ui.login

import androidx.lifecycle.*
import com.sikderithub.viewsgrow.Model.Login
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

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