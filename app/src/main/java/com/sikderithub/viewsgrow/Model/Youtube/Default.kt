package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Default (

  @SerializedName("url"    ) var url    : String? = null,
  @SerializedName("width"  ) var width  : Int?    = null,
  @SerializedName("height" ) var height : Int?    = null

)