package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class Localized (

  @SerializedName("title"       ) var title       : String? = null,
  @SerializedName("description" ) var description : String? = null

)