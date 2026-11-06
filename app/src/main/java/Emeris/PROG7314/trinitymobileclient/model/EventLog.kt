package Emeris.PROG7314.trinitymobileclient.model

import com.google.gson.annotations.SerializedName

data class EventLog(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("type")      val type: String,
    @SerializedName("message")   val message: String
)
