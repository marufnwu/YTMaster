package com.sikderithub.viewsgrow.Model

import com.google.gson.annotations.SerializedName

class Link(
    @SerializedName("id"        ) var id        : Int?    = null,
    @SerializedName("userId"    ) var userId    : Int?    = null,
    @SerializedName("domain"    ) var domain    : String? = null,
    @SerializedName("subdomain" ) var subdomain : String? = null,
    @SerializedName("ref"       ) var ref       : String? = null,
    @SerializedName("nLink"     ) var nLink     : String? = null,
    @SerializedName("oLink"     ) var oLink     : String? = null,
    @SerializedName("intent"    ) var intent    : String? = null,
    @SerializedName("type"      ) var type      : String? = null,
    @SerializedName("metaTitle" ) var metaTitle : String? = null,
    @SerializedName("metaDesc"  ) var metaDesc  : String? = null,
    @SerializedName("metaImg"   ) var metaImg   : String? = null,
    @SerializedName("status"    ) var status    : Int?    = null,
    @SerializedName("click"    ) var click    : Int?    = null,
    @SerializedName("chLogo"    ) var chLogo    : String?    = null,
    @SerializedName("chName"    ) var chName    : String?    = null,
)