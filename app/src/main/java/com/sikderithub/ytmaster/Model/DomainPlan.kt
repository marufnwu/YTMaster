package com.sikderithub.ytmaster.Model

import com.google.gson.annotations.SerializedName

data class DomainPlan(
    @SerializedName("id"          ) var id         : Int = 0,
    @SerializedName("price"       ) var price      : Int = 0,
    @SerializedName("vat"         ) var vat        : Int = 0,
    @SerializedName("total_price" ) var totalPrice : Int = 0,
    @SerializedName("validity"    ) var validity   : Int = 0,
    @SerializedName("status"      ) var status     : Int = 0
)
