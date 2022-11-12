package com.sikderithub.ytmaster.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Thumbnails (

  @SerializedName("default" ) var default : Default? = Default(),
  @SerializedName("medium"  ) var medium  : Medium?  = Medium(),
  @SerializedName("high"    ) var high    : High?    = High()

)