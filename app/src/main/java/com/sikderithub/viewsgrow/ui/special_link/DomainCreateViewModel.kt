package com.sikderithub.viewsgrow.ui.special_link

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.CreateNewDomainPage
import com.sikderithub.viewsgrow.Model.MyResponse
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class DomainCreateViewModel(myApi: MyApi) : ViewModel() {
    val ytRepo = YtRepo(myApi)

    init {
        getPage()
    }

    private val _domainPage by lazy{
        MutableLiveData<ScreenState<CreateNewDomainPage>>()
    }

    private val _requestDomain by lazy{
        MutableLiveData<ScreenState<MyResponse>>()
    }

    val requestDomain: LiveData<ScreenState<MyResponse>>
        get() = _requestDomain

    val getDomainPage : LiveData<ScreenState<CreateNewDomainPage>>
        get() = _domainPage

    fun getPage(){
        Log.d("Called", "YesCallded")
        Coroutines.main {
            _domainPage.postValue(ScreenState.Loading())
            val res = ytRepo.getCreateNewDomainPage()
            if(res.isSuccessful && res.body()!=null){
                val page = res.body()!!
                if(!page.error){
                    _domainPage.postValue(ScreenState.Success(data = page.data))
                }else{
                    _domainPage.postValue(ScreenState.Error(message = page.msg))
                }
            }else{
                _domainPage.postValue(ScreenState.Error(message = "Something went wrong"))
            }
        }
    }

    fun requestDomain(chName: String, planId:Int){
        Coroutines.main {
            _requestDomain.postValue(ScreenState.Loading())
            val res = ytRepo.requestDomain(chName,planId)

            if(res.isSuccessful && res.body()!=null){
                _requestDomain.postValue(ScreenState.Success(data = res.body()))
            }else{
                _requestDomain.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }

}