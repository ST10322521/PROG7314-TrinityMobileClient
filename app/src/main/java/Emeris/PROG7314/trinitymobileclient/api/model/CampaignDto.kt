package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class CampaignDto(
    @SerializedName("id")   val id: Int    = 0,
    @SerializedName("name") val name: String = ""
)
