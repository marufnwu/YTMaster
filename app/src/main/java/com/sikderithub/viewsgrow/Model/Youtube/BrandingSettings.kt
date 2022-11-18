package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class BrandingSettings (

  @SerializedName("channel" ) var channel : Channel? = Channel(),
  @SerializedName("image"   ) var image   : Image?   = Image()

)