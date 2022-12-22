package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

data class HomePage(
    @SerializedName("mainBanner") val mainBanner: Banner,
    @SerializedName("honeBannerLeft") val honeBannerLeft: Banner,
    @SerializedName("homeBannerRight") val homeBannerRight: Banner,

    @SerializedName("facebook") val facebookLink: String?,
    @SerializedName("youtube") val youtubeLink: String?,
    @SerializedName("instagram") val instagramLink: String?,
)