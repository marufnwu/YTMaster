package com.sikderithub.ytmaster.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sikderithub.ytmaster.Model.Youtube.ChanelInfo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.ui.userdetails.UserDetailsViewModel
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.MyApp
import com.sikderithub.ytmaster.utils.ScreenState

class ProfileViewModel(private val myApi: MyApi) : ViewModel() {

    val channelInfo = MutableLiveData<ScreenState<ChanelInfo>>()

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