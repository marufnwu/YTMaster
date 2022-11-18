package com.sikderithub.viewsgrow.ui.channel_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.HighLightedChannel
import com.sikderithub.viewsgrow.Model.Paging
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class ChannelListViewModel(myApi: MyApi) : ViewModel() {

    val ytRepo = YtRepo(myApi)

    val highLightedChannels = MutableLiveData<ScreenState<Paging<HighLightedChannel>>>()

    init {
        getHighLightedChannel(1, 0)
    }

    fun getHighLightedChannel(currPage:Int, totalPage:Int){
        highLightedChannels.postValue(ScreenState.Loading())
        Coroutines.main {
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
        }
    }

}