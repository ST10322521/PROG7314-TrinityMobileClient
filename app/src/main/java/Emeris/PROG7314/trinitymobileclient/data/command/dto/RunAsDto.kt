package Emeris.PROG7314.trinitymobileclient.data.command.dto

data class RunAsDto(
    val domainUser: String,
    val password: String,
    val command: String,
    val arguements: String?
)
