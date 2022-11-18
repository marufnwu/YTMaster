package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

data class LinkRef(
    @SerializedName("error") val error: Boolean = false,
    @SerializedName("msg") val msg: String = "",
    @SerializedName("ref") val ref: String = ""
)