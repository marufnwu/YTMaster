package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Snippet (

  @SerializedName("title"       ) var title       : String?     = null,
  @SerializedName("description" ) var description : String?     = null,
  @SerializedName("customUrl"   ) var customUrl   : String?     = null,
  @SerializedName("publishedAt" ) var publishedAt : String?     = null,
  @SerializedName("thumbnails"  ) var thumbnails  : Thumbnails? = Thumbnails(),
  @SerializedName("localized"   ) var localized   : Localized?  = Localized(),
  @SerializedName("country"     ) var country     : String?     = null

)