package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

data class Paging<T>(
    @SerializedName("currPage"  ) var currPage  : Int?             = null,
    @SerializedName("totalPage" ) var totalPage : Int?             = null,
    @SerializedName("maindata"     ) var maindata     : MutableList<T>? =null
)