package com.sikderithub.viewsgrow.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.*
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.MyException
import com.sikderithub.viewsgrow.utils.ScreenState
import kotlin.Exception

class MainActivityViewModel(private val  myApi: MyApi) : ViewModel() {
    private val ytRepo = YtRepo(myApi)
    private val _isUserRegistered : MutableLiveData<ScreenState<Boolean>> = MutableLiveData()

    val thumbLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()
    val homePage : MutableLiveData<ScreenState<HomePage>> = MutableLiveData()

    val userList = MutableLiveData<ScreenState<MutableList<Profile>>>()
    val highLightedChannels = MutableLiveData<ScreenState<Paging<HighLightedChannel>>>()

    private val _recentLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()
    val recentLinks : LiveData<ScreenState<Paging<Link>>>
        get() = _recentLinks

    init {
        homeData()
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
    private fun getThumbLinks(){
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

    fun homeData(){
        Coroutines.main {
            homePage.postValue(ScreenState.Loading())
            val res = ytRepo.getHomePage()
            if(res.isSuccessful && res.body()!=null){
                val result = res.body()!!
                if(!result.error){
                    homePage.postValue(ScreenState.Success(data = result.data))
                }else{
                    homePage.postValue(ScreenState.Error(message = result.msg))
                }
            }else{
                homePage.postValue(ScreenState.Loading())
            }
        }
    }


}