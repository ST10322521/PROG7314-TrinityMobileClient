package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token")       val token: String?,
    @SerializedName("accessToken") val accessToken: String?
) {
    val bearerToken: String? get() = token ?: accessToken
}
