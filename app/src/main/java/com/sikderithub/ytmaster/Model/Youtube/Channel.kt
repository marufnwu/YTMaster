package com.sikderithub.ytmaster.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Channel (

  @SerializedName("title"               ) var title               : String? = null,
  @SerializedName("description"         ) var description         : String? = null,
  @SerializedName("keywords"            ) var keywords            : String? = null,
  @SerializedName("unsubscribedTrailer" ) var unsubscribedTrailer : String? = null,
  @SerializedName("country"             ) var country             : String? = null

)