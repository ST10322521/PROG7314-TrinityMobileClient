package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.CreateHttpListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateHttpsListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateSmbListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CreateTcpListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.UpdateHttpListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.UpdateHttpsListenerRequest
import Emeris.PROG7314.trinitymobileclient.api.model.ListenerDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ListenerApiService {

    @GET("api/v1/listeners")
    suspend fun getListeners(): Response<List<ListenerDto>>

    @POST("api/v1/listeners/http")
    suspend fun createHttp(@Body body: CreateHttpListenerRequest): Response<ResponseBody>

    @POST("api/v1/listeners/https")
    suspend fun createHttps(@Body body: CreateHttpsListenerRequest): Response<ResponseBody>

    @POST("api/v1/listeners/tcp")
    suspend fun createTcp(@Body body: CreateTcpListenerRequest): Response<ResponseBody>

    @POST("api/v1/listeners/smb")
    suspend fun createSmb(@Body body: CreateSmbListenerRequest): Response<ResponseBody>

    @GET("api/v1/listeners/http/{ID}")
    suspend fun getHttp(@Path("ID") id: Int): Response<ResponseBody>

    @PUT("api/v1/listeners/http/{ID}")
    suspend fun updateHttp(@Path("ID") id: Int, @Body body: UpdateHttpListenerRequest): Response<ResponseBody>

    @PUT("api/v1/listeners/https/{ID}")
    suspend fun updateHttps(@Path("ID") id: Int, @Body body: UpdateHttpsListenerRequest): Response<ResponseBody>

    @POST("api/v1/listeners/http/restart/{ID}")
    suspend fun restartHttp(@Path("ID") id: Int): Response<ResponseBody>

    @DELETE("api/v1/listeners/{ID}")
    suspend fun delete(@Path("ID") id: Int): Response<ResponseBody>
}
