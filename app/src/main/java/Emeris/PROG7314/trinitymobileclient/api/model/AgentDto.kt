package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class AgentDto(
    @SerializedName("id")         val id: String?      = null,
    @SerializedName("hostname")   val hostname: String? = null,
    @SerializedName("username")   val username: String? = null,
    @SerializedName("os")         val os: String?       = null,
    @SerializedName("externalIp") val externalIp: String? = null,
    @SerializedName("internalIp") val internalIp: String? = null,
    @SerializedName("lastSeen")   val lastSeen: String? = null,
    @SerializedName("isActive")   val isActive: Boolean? = null,
    @SerializedName("pid")        val pid: Int?         = null
)
