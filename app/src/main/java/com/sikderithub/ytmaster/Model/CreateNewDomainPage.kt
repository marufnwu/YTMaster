package com.sikderithub.ytmaster.Model

import com.google.gson.annotations.SerializedName

data class CreateNewDomainPage (
    @SerializedName("plan")     var planList     : List<DomainPlan>,
    @SerializedName("banner")   var banner   : Banner,
)