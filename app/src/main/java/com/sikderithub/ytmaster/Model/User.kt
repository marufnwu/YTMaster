package com.sikderithub.ytmaster.Model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class User(
    @SerializedName("id"          ) var id         : Int?    = null,
    @SerializedName("full_name"   ) var fullName   : String? = null,
    @SerializedName("email"       ) var email      : String? = null,
    @SerializedName("uId"         ) var uId        : String? = null,
    @SerializedName("phone"       ) var phone      : String? = null,
    @SerializedName("join_date"   ) var joinDate   : String? = null,
    @SerializedName("last_active" ) var lastActive : String? = null,
    @SerializedName("profilePic"  ) var profilePic : String? = null,
    @SerializedName("fcm_token"   ) var fcmToken   : String? = null,
    @SerializedName("Country"     ) var Country    : String? = null,
    @SerializedName("State"       ) var State      : String? = null,
    @SerializedName("City"        ) var City       : String? = null,
    @SerializedName("chanel_link" ) var chanelLink : String? = null
)
