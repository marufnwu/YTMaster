package com.sikderithub.viewsgrow.ui.generate_link

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sikderithub.viewsgrow.Model.*
import com.sikderithub.viewsgrow.repo.YtRepo
import com.sikderithub.viewsgrow.repo.network.MyApi
import com.sikderithub.viewsgrow.utils.CommonMethod
import com.sikderithub.viewsgrow.utils.Coroutines
import com.sikderithub.viewsgrow.utils.MyException
import com.sikderithub.viewsgrow.utils.ScreenState

class GenerateLinkViewModel(private val myApi: MyApi) : ViewModel() {
    private val ytRepo = YtRepo(myApi)

    val domainSuggestions : MutableLiveData<ScreenState<List<String>>> by lazy{
        MutableLiveData<ScreenState<List<String>>>()
    }


    val fullLinkData: MutableLiveData<NewFullLink> by lazy {
        MutableLiveData<NewFullLink>()
    }

    val metaData =  MutableLiveData<GenerateLinkActivity.LinkMetaData>().apply {
        postValue(GenerateLinkActivity.LinkMetaData())
    }
    val customThumb: MutableLiveData<GenerateLinkActivity.CustomThumb> by lazy {
            MutableLiveData<GenerateLinkActivity.CustomThumb>()
    }

    val suffixCheck: MutableLiveData<ScreenState<Boolean>> by lazy {
        MutableLiveData<ScreenState<Boolean>>()
    }

    val publishLinkRes: MutableLiveData<ScreenState<String>> by lazy {
        MutableLiveData<ScreenState<String>>()
    }

    val addSubdomain = MutableLiveData<ScreenState<Subdomain>>()



    val suffix: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val _linkGenPage= MutableLiveData<ScreenState<LinkGenPage>>()

    val linkRef : LiveData<ScreenState<LinkGenPage>>
        get() = _linkGenPage

    private val _toast = MutableLiveData<String>()

    val toast: LiveData<String>
        get() = _toast

    fun getLinkGenPage(link: String) {



        Coroutines.main {
            try {
                _linkGenPage.postValue(ScreenState.Loading())
                val res = ytRepo.getLinkPage(link)
                if(res.isSuccessful && res.body()!=null){

                    val linkGenPageResponse = res.body()!!
                    if(!linkGenPageResponse.error){

                        _linkGenPage.postValue(ScreenState.Success(linkGenPageResponse.data))
                        setSuffix(linkGenPageResponse.data.uniqueRef)


                        val fullLink = NewFullLink(
                            domain = linkGenPageResponse.data.domains.takeWhile { it.main==1 }[0].name,
                            subdomain = null,
                            suffix = linkGenPageResponse.data.uniqueRef.ref,
                            type = CommonMethod.getLinkPlatform(link)
                        )

                        fullLinkData.postValue(fullLink)

                    }else{
                        _linkGenPage.postValue(ScreenState.Error(linkGenPageResponse.data, linkGenPageResponse.msg))
                    }


                }else{
                    _linkGenPage.postValue(ScreenState.Error(null, res.message()))
                }
            }catch (e : Exception){
                _linkGenPage.postValue(ScreenState.Error(null, e.message!!))
            }
        }
    }

    fun addSubdomain(name:String){
        try {
            Coroutines.main {
                addSubdomain.postValue(ScreenState.Loading())

                val res = ytRepo.addSubDomain(name)
                if(res.isSuccessful && res.body()!=null){
                    val subdomainRes = res.body()!!
                    if(!subdomainRes.error){
                        addSubdomain.postValue(ScreenState.Success(subdomainRes.data))
                    }else{
                        addSubdomain.postValue(ScreenState.Error(null, subdomainRes.msg))
                    }
                }else{
                    addSubdomain.postValue(ScreenState.Error(null, res.message()))
                }

            }
        }catch (e:Exception){
            addSubdomain.postValue(ScreenState.Error(null, e.message!!))
        }
    }

    private fun setSuffix(linkRef : LinkRef){
        suffix.postValue(linkRef.ref)
    }
    fun addNewLink(oLink:String){

        if(oLink.isEmpty()){
            publishLinkRes.postValue(ScreenState.Error(null, "Suffix must not empty"))
        }

        if(metaData.value==null || metaData.value!!.loading){
            _linkGenPage.postValue(ScreenState.Error(null, "Please wait. Meta data is fetching"))
            return
        }

        var metaTitle = ""
        var metaDes = ""
        var metaImg = ""

        metaData.value?.let {
            it.desc?.let {
                metaDes = it
            }

            it.title?.let {
                metaTitle = it
            }
            it.imageUrl?.let {
                metaImg = it
            }
        }


        customThumb.value?.let {
            if(it.isUse){
                //custom thumbnail enable
                it.url?.let {
                    metaImg  = it
                }

            }else{
                //not enable
            }
        }


        publishLinkRes.postValue(ScreenState.Loading())




        Coroutines.main {
            try{
                val linkData = fullLinkData.value!!
                val intent =  if (CommonMethod.getIntentLink(oLink) == null)  "" else CommonMethod.getIntentLink(oLink)!!
                val subDom  = if (linkData.subdomain == null)  "" else linkData.subdomain!!
                val linkType = if (linkData.type == null)  "" else linkData.type?.name!!
                val res = ytRepo.addNewLink(linkData.domain!! , subDom, linkData.suffix!!, oLink, linkData.toString(), intent, linkType, metaTitle, metaDes, metaImg)
                if(res.isSuccessful && res.body()!=null){
                    publishLinkRes.postValue(ScreenState.Success(res.body()!!.data))
                }else{
                    publishLinkRes.postValue(ScreenState.Error(message = "Something went wrong"))
                }
            }catch (e:Exception){
                publishLinkRes.postValue(ScreenState.Error(message = "Something went wrong"))
            }
        }
    }

    fun getDomainSuggestion(videoId:String){

        domainSuggestions.postValue(ScreenState.Loading())
        Coroutines.main {
            val res = ytRepo.getDomainSuggetions(videoId)
            if (res.isSuccessful && res.body()!=null){
                val body = res.body()!!
                if(!body.error){
                    domainSuggestions.postValue(ScreenState.Success(data = body.data))
                }else{
                    domainSuggestions.postValue(ScreenState.Error(message = body.msg))
                }
            }
        }
    }




}

