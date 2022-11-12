package com.sikderithub.ytmaster.Model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("error") val error: Boolean = true,
    @SerializedName("msg") val msg: String,
    @SerializedName("account") val account: Boolean = false,
    @SerializedName("token") val token: String,
    @SerializedName("profile") val profile: Profile?,
)
