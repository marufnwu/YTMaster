package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

data class HomePage(
    @SerializedName("mainBanner") val mainBanner: Banner
)