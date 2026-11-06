package Emeris.PROG7314.trinitymobileclient.data.command.dto

data class DownloadDTO(
    val path: String,
    val destinationPath: String,
    val data: String,
    val offset: Long,
    val final: Boolean
)
