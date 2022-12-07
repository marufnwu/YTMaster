package com.sikderithub.viewsgrow.ui.special_link

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.CreateNewDomainPage
import com.sikderithub.viewsgrow.Model.MyResponse
import com.sikderithub.viewsgrow.Model.Transaction
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class DomainCreateViewModel(myApi: MyApi) : ViewModel() {
    val ytRepo = YtRepo(myApi)

    private val _domainPage by lazy{
        MutableLiveData<ScreenState<CreateNewDomainPage>>()
    }

    private val _subDomainPage by lazy{
        MutableLiveData<ScreenState<CreateNewDomainPage>>()
    }

    private val _requestDomain by lazy{
        MutableLiveData<ScreenState<Transaction>>()
    }
    private val _requestSubDomain by lazy{
        MutableLiveData<ScreenState<Transaction>>()
    }

    val requestDomain: LiveData<ScreenState<Transaction>>
        get() = _requestDomain


    val requestSubDomain: LiveData<ScreenState<Transaction>>
        get() = _requestSubDomain

    val getDomainPage : LiveData<ScreenState<CreateNewDomainPage>>
        get() = _domainPage

    val getSubDomainPage : LiveData<ScreenState<CreateNewDomainPage>>
        get() = _subDomainPage

    fun getPage(){
        Log.d("Called", "YesCallded")
        Coroutines.main {
           try {
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
           }catch (e:Exception){
               _domainPage.postValue(ScreenState.Error(message = "Something went wrong"))
           }
        }
    }

    fun getSubDomainPage(){
        Coroutines.main {
            try {
                _subDomainPage.postValue(ScreenState.Loading())
                val res = ytRepo.getCreateNewSubDomainPage()
                if(res.isSuccessful && res.body()!=null){
                    val page = res.body()!!
                    if(!page.error){
                        _subDomainPage.postValue(ScreenState.Success(data = page.data))
                    }else{
                        _subDomainPage.postValue(ScreenState.Error(message = page.msg))
                    }
                }else{
                    _subDomainPage.postValue(ScreenState.Error(message = "Something went wrong"))
                }
            }catch (e:Exception){
                _subDomainPage.postValue(ScreenState.Error(message = "Something went wrong"))
            }
        }
    }

    fun requestDomain(chName: String){
        Coroutines.main {
            try {
                _requestDomain.postValue(ScreenState.Loading())
                val res = ytRepo.requestDomain(chName)

                if(res.isSuccessful && res.body()!=null){

                    if(!res.body()!!.error){
                        _requestDomain.postValue(ScreenState.Success(data = res.body()!!.data))

                    }else{
                        _requestDomain.postValue(ScreenState.Error(message = res.body()!!.msg))
                    }

                }else{
                    _requestDomain.postValue(ScreenState.Error(message = res.message()))
                }
            }catch (e:Exception){
                _requestDomain.postValue(ScreenState.Error(message = "Something went wrong"))
            }
        }
    }

    fun requestSubDomain(chName: String){
        Coroutines.main {
            try {
                _requestSubDomain.postValue(ScreenState.Loading())
                val res = ytRepo.requestSubDomain(chName)

                if(res.isSuccessful && res.body()!=null){

                    if(!res.body()!!.error){
                        _requestSubDomain.postValue(ScreenState.Success(data = res.body()!!.data))

                    }else{
                        _requestSubDomain.postValue(ScreenState.Error(message = res.body()!!.msg))
                    }

                }else{
                    _requestSubDomain.postValue(ScreenState.Error(message = res.message()))
                }
            }catch (e:Exception){
                _requestSubDomain.postValue(ScreenState.Error(message = "Something went wrong"))
            }
        }
    }

}