package com.sikderithub.ytmaster.repo

import com.sikderithub.ytmaster.Model.*
import com.sikderithub.ytmaster.repo.network.MyApi
import retrofit2.Response

class YtRepo(private val api: MyApi) {
    suspend fun isUserRegistered(): Response<GenericResponse<Profile>>{
        return api.isUserRegistered()
    }

    suspend fun getUniqueRef(): Response<LinkRef>{
        return api.generateUniqueRef()
    }

    suspend fun getLinkPage():Response<LinkGenPageResponse>{
        return  api.linkPage()
    }

    suspend fun addSubDomain(name:String):Response<GenericResponse<Subdomain>>{
        return  api.addSubdomain(name)
    }

    suspend fun login(gToken:String): Response<Login> {
        return api.login(gToken)
    }

    suspend fun signup(
        fullname: String,
        email: String,
        phone: String,
        city: String,
        state: String,
        country: String,
        youtubeChanelLink: String,
        profilePic: String,
        googleId: String?
    ): Response<Login> {
        return api.signup(fullname, email ,phone , city, state , country ,youtubeChanelLink, profilePic, googleId)
    }

    suspend fun addNewLink(domain : String, subdomain:String, suffix: String, oLink:String, nLink:String, intent: String, type:String, metaTitle:String, metaDesc:String, metaImg:String): Response<GenericResponse<String>> {
        return api.addNewLink(domain, subdomain, suffix, oLink, nLink, intent, type, metaTitle, metaDesc, metaImg)
    }

    suspend fun getLinks(currPage:Int = 1 , totalPage:Int = 0, unique:Int=0): Response<GenericResponse<Paging<Link>>> {
        return api.getLink(currPage, totalPage, unique)
    }

    suspend fun getThumbLinks(currPage:Int = 1 , totalPage:Int = 0, unique: Int=0): Response<GenericResponse<Paging<Link>>> {
        return api.getAllUserLink(currPage, totalPage, unique)
    }

    suspend fun getHighLightedUser(): Response<GenericResponse<MutableList<Profile>>> {
        return api.highLightedUser()
    }

    suspend fun getHighLightedChannel(currPage: Int = 1, totalPage: Int=0): Response<GenericResponse<Paging<HighLightedChannel>>> {
        return api.getHighlightedChannel(currPage, totalPage)
    }

    suspend fun getCreateNewDomainPage(): Response<GenericResponse<CreateNewDomainPage>> {
        return api.createNewDomainPage()
    }

    suspend fun requestDomain( chName:String,  planid:Int ): Response<MyResponse> {
        return api.createDomainRef(chName, planid)
    }

}