package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class BofRequest(
    @SerializedName("bofPath")    val bofPath: String?,
    @SerializedName("entrypoint") val entrypoint: String?,
    @SerializedName("arguements") val arguements: String?
)

data class CancelDownloadRequest(
    @SerializedName("taskID") val taskId: Int
)

data class UploadRequest(
    @SerializedName("path")   val path: String?,
    @SerializedName("offset") val offset: Long
)
