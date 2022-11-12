package com.sikderithub.ytmaster.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.ytmaster.Model.HighLightedChannel
import com.sikderithub.ytmaster.Model.Link
import com.sikderithub.ytmaster.Model.Paging
import com.sikderithub.ytmaster.Model.Profile
import com.sikderithub.ytmaster.repo.YtRepo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.MyApp
import com.sikderithub.ytmaster.utils.MyException
import com.sikderithub.ytmaster.utils.ScreenState
import kotlin.Exception

class MainActivityViewModel(private val  myApi: MyApi) : ViewModel() {
    private val ytRepo = YtRepo(myApi)
    private val _isUserRegistered : MutableLiveData<ScreenState<Boolean>> = MutableLiveData()

    private val _recentLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()
    val thumbLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()

    val userList = MutableLiveData<ScreenState<MutableList<Profile>>>()
    val highLightedChannels = MutableLiveData<ScreenState<Paging<HighLightedChannel>>>()


    val recentLinks : LiveData<ScreenState<Paging<Link>>>
        get() = _recentLinks

    init {
        getHighLightedChannel()
        //getRecentLinks()
        //getUsers()
        getThumbLinks()
    }

    fun getRecentLinks(){
        _recentLinks.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getLinks()
            if(res.isSuccessful && res.body()!=null){
                val resBody = res.body()!!
                if(!resBody.error){
                    _recentLinks.postValue(ScreenState.Success(resBody.data))
                }else{
                    _recentLinks.postValue(ScreenState.Error(message = resBody.msg))
                }
            }else{
                _recentLinks.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }
    fun getThumbLinks(){
            thumbLinks.postValue(ScreenState.Loading())
            Coroutines.main {
                try{
                    val res = ytRepo.getThumbLinks(unique = 1)
                    if(res.isSuccessful && res.body()!=null){
                        val resBody = res.body()!!
                        if(!resBody.error){
                            thumbLinks.postValue(ScreenState.Success( resBody.data))
                        }else{
                            thumbLinks.postValue(ScreenState.Error(message = resBody.msg))
                        }
                    }else{
                        thumbLinks.postValue(ScreenState.Error(message = res.message()))
                    }
                }catch (e: Exception){
                    MyException.showDialog(e.message)
                }
            }
        }


    private fun getUsers(){
        userList.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getHighLightedUser()
            if(res.isSuccessful && res.body()!=null){
                val resBody = res.body()!!
                if(!resBody.error){
                    userList.postValue(ScreenState.Success(resBody.data))
                }else{
                    userList.postValue(ScreenState.Error(message = resBody.msg))
                }
            }else{
                userList.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }

    private fun getHighLightedChannel(){
        highLightedChannels.postValue(ScreenState.Loading())
        Coroutines.main {
            try {
                val res = ytRepo.getHighLightedChannel()
                if(res.isSuccessful && res.body()!=null){
                    val resBody = res.body()!!
                    if(!resBody.error){
                        highLightedChannels.postValue(ScreenState.Success(resBody.data))
                    }else{
                        highLightedChannels.postValue(ScreenState.Error(message = resBody.msg))
                    }
                }else{
                    highLightedChannels.postValue(ScreenState.Error(message = res.message()))
                }
            }catch (e:Exception){
                MyException.showDialog(e.message)
            }
        }
    }

    fun isUserRegistered(email:String){

    }


}