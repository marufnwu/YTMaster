package com.sikderithub.ytmaster.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Statistics (

  @SerializedName("viewCount"             ) var viewCount             : String?  = null,
  @SerializedName("subscriberCount"       ) var subscriberCount       : String?  = null,
  @SerializedName("hiddenSubscriberCount" ) var hiddenSubscriberCount : Boolean? = null,
  @SerializedName("videoCount"            ) var videoCount            : String?  = null

)