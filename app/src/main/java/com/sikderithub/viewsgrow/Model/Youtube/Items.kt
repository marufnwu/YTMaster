package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Items (

  @SerializedName("kind"             ) var kind             : String?           = null,
  @SerializedName("etag"             ) var etag             : String?           = null,
  @SerializedName("id"               ) var id               : String?           = null,
  @SerializedName("snippet"          ) var snippet          : Snippet?          = Snippet(),
  @SerializedName("statistics"       ) var statistics       : Statistics?       = Statistics(),
  @SerializedName("brandingSettings" ) var brandingSettings : BrandingSettings? = BrandingSettings()

)