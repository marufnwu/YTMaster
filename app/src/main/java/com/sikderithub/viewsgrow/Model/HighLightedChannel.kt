package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName
import com.sikderithub.viewsgrow.Model.Youtube.Statistics

data class HighLightedChannel(
    @SerializedName("id"         ) var id         : Int?        = null,
    @SerializedName("chId"       ) var chId       : String?     = null,
    @SerializedName("userId"     ) var userId     : Int?        = null,
    @SerializedName("name"       ) var name       : String?     = null,
    @SerializedName("logo"       ) var logo       : String?     = null,
    @SerializedName("publish"    ) var publish    : String?     = null,
    @SerializedName("statistics" ) var statistics : Statistics? = null
)