package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class OperatorDto(
    @SerializedName("id")        val id: Int?     = null,
    @SerializedName("username")  val username: String? = null,
    @SerializedName("lastLogin") val lastLogin: String? = null
)
