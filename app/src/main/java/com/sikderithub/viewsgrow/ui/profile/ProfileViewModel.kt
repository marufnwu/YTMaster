package com.sikderithub.viewsgrow.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sikderithub.viewsgrow.Model.Link
import com.sikderithub.viewsgrow.Model.Paging
import com.sikderithub.viewsgrow.Model.Youtube.ChanelInfo
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class ProfileViewModel(private val myApi: MyApi) : ViewModel() {

    val ytRepo = YtRepo(myApi)

    val channelInfo = MutableLiveData<ScreenState<ChanelInfo>>()

    private val _myLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()

    val myLinks : LiveData<ScreenState<Paging<Link>>>
        get() = _myLinks

    init {
        getOwnLinks()
    }

    fun getChanelInfo(channel:String){
        channelInfo.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = myApi.chanelinfo(channel)
            if(res.isSuccessful && res.body()!=null){
                channelInfo.postValue(ScreenState.Success(res.body()!!))
            }else{
                channelInfo.postValue(ScreenState.Error(null, "No Information found"))
            }
        }
    }

    fun getOwnLinks(){
        _myLinks.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getLinks()
            if(res.isSuccessful && res.body()!=null){
                val resBody = res.body()!!
                if(!resBody.error){
                    _myLinks.postValue(ScreenState.Success(resBody.data))
                }else{
                    _myLinks.postValue(ScreenState.Error(message = resBody.msg))
                }
            }else{
                _myLinks.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }

}

class ProfileViewModelProvider(private val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}