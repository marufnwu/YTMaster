package com.sikderithub.viewsgrow.Model.Youtube

import com.google.gson.annotations.SerializedName


data class ChanelInfo (

  @SerializedName("kind"     ) var kind     : String?          = null,
  @SerializedName("etag"     ) var etag     : String?          = null,
  @SerializedName("pageInfo" ) var pageInfo : PageInfo?        = PageInfo(),
  @SerializedName("items"    ) var items    : ArrayList<Items>? = arrayListOf()

)