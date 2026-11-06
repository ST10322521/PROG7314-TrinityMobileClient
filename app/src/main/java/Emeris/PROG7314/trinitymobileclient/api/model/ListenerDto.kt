package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class ListenerDto(
    @SerializedName("id")       val id: Int?      = null,
    @SerializedName("name")     val name: String? = null,
    @SerializedName("type")     val type: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)
