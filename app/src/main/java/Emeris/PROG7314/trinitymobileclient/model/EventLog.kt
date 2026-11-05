package Emeris.PROG7314.trinitymobileclient.model

import com.google.gson.annotations.SerializedName

/**
 * Received from the hub when the server pushes notifications
 */
data class EventLog(
    @SerializedName("campaignID") val campaignId: String,
    @SerializedName("message")    val message: String,
    @SerializedName("level")      val level: String,
    @SerializedName("timestamp")  val timestamp: String
)
