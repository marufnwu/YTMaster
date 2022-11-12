package com.sikderithub.ytmaster.Model.Youtube

import com.google.gson.annotations.SerializedName


data class PageInfo (

  @SerializedName("totalResults"   ) var totalResults   : Int? = null,
  @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)