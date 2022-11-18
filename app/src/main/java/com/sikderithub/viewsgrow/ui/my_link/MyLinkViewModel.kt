package com.sikderithub.viewsgrow.ui.my_link

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.Link
import com.sikderithub.viewsgrow.Model.Paging
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.ScreenState

class MyLinkViewModel(val myApi: MyApi) : ViewModel() {
    val ytRepo = YtRepo(myApi)

    private val _myLinks : MutableLiveData<ScreenState<Paging<Link>>> = MutableLiveData()
    val myLinks : LiveData<ScreenState<Paging<Link>>>
        get() = _myLinks


    init {
        getOwnLinks(1, 0)
    }

    fun getOwnLinks(currPage: Int, totalPage:Int){
        _myLinks.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getLinks(currPage = currPage, totalPage = totalPage)
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