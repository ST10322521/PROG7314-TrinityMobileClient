package Emeris.PROG7314.trinitymobileclient.model

import com.google.gson.annotations.SerializedName

data class TaskNotification(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("taskID")    val taskId: Int,
    @SerializedName("message")   val message: String,
    @SerializedName("status")    val status: String
)
