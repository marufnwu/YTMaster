package com.sikderithub.ytmaster.ui.all_link

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.ytmaster.Model.Link
import com.sikderithub.ytmaster.Model.Paging
import com.sikderithub.ytmaster.repo.YtRepo
import com.sikderithub.ytmaster.repo.network.MyApi
import com.sikderithub.ytmaster.utils.Coroutines
import com.sikderithub.ytmaster.utils.ScreenState

class AllLinkViewModel(val myApi: MyApi) : ViewModel() {
    val ytRepo = YtRepo(myApi)

    val thumbLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()


    init {
        getThumbLinks(1, 0)
    }

    fun getThumbLinks(currPage:Int, totalPage:Int){
        thumbLinks.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getThumbLinks(currPage, totalPage)
            if(res.isSuccessful && res.body()!=null){
                val resBody = res.body()!!
                if(!resBody.error){
                    thumbLinks.postValue(ScreenState.Success(resBody.data))
                }else{
                    thumbLinks.postValue(ScreenState.Error(message = resBody.msg))
                }
            }else{
                thumbLinks.postValue(ScreenState.Error(message = res.message()))
            }
        }
    }
}