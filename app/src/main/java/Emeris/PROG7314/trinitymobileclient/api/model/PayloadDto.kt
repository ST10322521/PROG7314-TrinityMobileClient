package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class PayloadDto(
    @SerializedName("id")               val id: Int?           = null,
    @SerializedName("name")             val name: String?      = null,
    @SerializedName("type")             val type: String?      = null,  // PayloadType enum name: WINDOWS_EXE etc.
    @SerializedName("architecture")     val architecture: String? = null, // Arch enum name: x64, x86, x32
    @SerializedName("listenerID")       val listenerId: Int?   = null,
    @SerializedName("campaignID")       val campaignId: Int?   = null,
    @SerializedName("operatorID")       val operatorId: Int?   = null,
    @SerializedName("isStageless")      val isStageless: Boolean? = null,
    @SerializedName("sleep")            val sleep: Int?        = null,
    @SerializedName("jitter")           val jitter: Int?       = null,
    @SerializedName("createdAt")        val createdAt: String? = null
)
