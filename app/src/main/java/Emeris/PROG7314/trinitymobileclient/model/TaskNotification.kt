package Emeris.PROG7314.trinitymobileclient.model

import com.google.gson.annotations.SerializedName

/**
 * Received from the hub when the server pushes task responses.
 */
data class TaskNotification(
    @SerializedName("agentID")   val agentId: String,
    @SerializedName("output")    val output: String,
    @SerializedName("status")    val status: String,
    @SerializedName("timestamp") val timestamp: String
)
