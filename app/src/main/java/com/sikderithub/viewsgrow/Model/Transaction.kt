package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("reference") val reference:String = "",
    @SerializedName("orderId") val orderId:String = "",
    @SerializedName("type") val type:String = "",
    @SerializedName("gateway") val gateway:String = "",
    @SerializedName("link") val link:String = "",
)