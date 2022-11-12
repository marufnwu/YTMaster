package com.sikderithub.ytmaster.Model

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("id"         ) val id        : Int,
    @SerializedName("page"       ) var page      : String,
    @SerializedName("image_url"  ) var imageUrl  : String,
    @SerializedName("action_url" ) var actionUrl : String,
    @SerializedName("status"     ) var status    : Int,
    @SerializedName("error"      ) var error     : Boolean,
    @SerializedName("msg"        ) var msg       : String
)
