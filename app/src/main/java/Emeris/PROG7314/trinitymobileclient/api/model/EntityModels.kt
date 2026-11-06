package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class AdminRequest(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("username")  val username: String,
    @SerializedName("password")  val password: String,
    @SerializedName("lastLogin") val lastLogin: String? = null
)

data class OperatorRequest(
    @SerializedName("id")        val id: Int = 0,
    @SerializedName("username")  val username: String,
    @SerializedName("password")  val password: String,
    @SerializedName("lastLogin") val lastLogin: String? = null
)

data class CampaignRequest(
    @SerializedName("id")   val id: Int = 0,
    @SerializedName("name") val name: String
)

data class PayloadCreationRequest(
    @SerializedName("name")             val name: String?,
    @SerializedName("operatorID")       val operatorId: Int,
    @SerializedName("sleep")            val sleep: Int,
    @SerializedName("jitter")           val jitter: Int,
    @SerializedName("listenerID")       val listenerId: Int,
    @SerializedName("campaignID")       val campaignId: Int,
    @SerializedName("architecture")     val architecture: Int,
    @SerializedName("type")             val type: Int,
    @SerializedName("rotationStrategy") val rotationStrategy: Int,
    @SerializedName("isStageless")      val isStageless: Boolean
)
