package Emeris.PROG7314.trinitymobileclient.api.model

import com.google.gson.annotations.SerializedName

data class HttpConfig(
    @SerializedName("bindPort")          val bindPort: Int = 0,
    @SerializedName("c2Port")            val c2Port: Int = 0,
    @SerializedName("userAgent")         val userAgent: String? = null,
    @SerializedName("header")            val header: String? = null,
    @SerializedName("hosts")             val hosts: List<String>? = null,
    @SerializedName("rotationStrategy")  val rotationStrategy: String? = null,
    @SerializedName("maxRetryStrategy")  val maxRetryStrategy: String? = null
)

data class HttpsConfig(
    @SerializedName("c2Port")            val c2Port: Int = 0,
    @SerializedName("userAgent")         val userAgent: String? = null,
    @SerializedName("header")            val header: String? = null,
    @SerializedName("hosts")             val hosts: List<String>? = null,
    @SerializedName("rotationStrategy")  val rotationStrategy: String? = null,
    @SerializedName("maxRetryStrategy")  val maxRetryStrategy: String? = null
)

data class TcpConfig(@SerializedName("port") val port: Int = 0)
data class SmbConfig(@SerializedName("pipe") val pipe: String? = null)

data class CreateHttpListenerRequest(
    @SerializedName("name")   val name: String?,
    @SerializedName("config") val config: HttpConfig
)

data class CreateHttpsListenerRequest(
    @SerializedName("name")   val name: String?,
    @SerializedName("config") val config: HttpsConfig
)

data class CreateTcpListenerRequest(
    @SerializedName("name")   val name: String?,
    @SerializedName("config") val config: TcpConfig
)

data class CreateSmbListenerRequest(
    @SerializedName("name")   val name: String?,
    @SerializedName("config") val config: SmbConfig
)

data class UpdateHttpListenerRequest(@SerializedName("config") val config: HttpConfig)
data class UpdateHttpsListenerRequest(@SerializedName("config") val config: HttpsConfig)
