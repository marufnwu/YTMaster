package com.sikderithub.viewsgrow.utils

import android.util.Log
import com.sikderithub.viewsgrow.Model.Profile
import io.paperdb.Paper

object LocalDB {

    const val TOKEN_KEY = "10001"
    const val USER_ID_KEY = "10002"
    const val PROFILE_KEY = "10003"
    const val POLICY_KEY = "10004"

    fun saveAccessTokenAndUserId(token : String, userId: Int){
        Paper.book().write(TOKEN_KEY, token)
        Paper.book().write(USER_ID_KEY, userId)

        MyApp.updateFcmToken()

    }

    fun getAccessToken(): String? {
        return Paper.book().read<String?>(TOKEN_KEY, null)
    }

    fun getUserId(): Int?{
        val uid =  Paper.book().read<Int?>(USER_ID_KEY, null)
        return uid
    }

    fun saveAccessToken(token : String){
        Paper.book().write(TOKEN_KEY, token)
    }

    fun saveUserId(userId : Int){
        Paper.book().write(USER_ID_KEY, userId)
    }

    fun saveProfile(profile: Profile){
        Paper.book().write(PROFILE_KEY, profile)
    }

    fun getProfile():Profile?{
        return Paper.book().read<Profile?>(PROFILE_KEY, null)
    }

    fun removeLogin(){
        Paper.book().delete(USER_ID_KEY)
        Paper.book().delete(PROFILE_KEY)
        Paper.book().delete(TOKEN_KEY)
    }

    fun setPolicyRead(isRead:Boolean){
        Paper.book().write(POLICY_KEY, isRead)
    }

    fun getPolicyRead(): Boolean {
        return Paper.book().read<Boolean>(POLICY_KEY, false)!!
    }


}