package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.PayloadCreationRequest
import Emeris.PROG7314.trinitymobileclient.api.model.PayloadDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PayloadApiService {

    @GET("api/v1/payloads")
    suspend fun getPayloads(): Response<List<PayloadDto>>

    @GET("api/v1/payloads/{payloadID}")
    suspend fun getPayload(@Path("payloadID") payloadId: Int): Response<ResponseBody>

    @POST("api/v1/payloads/generate")
    suspend fun generatePayload(@Body body: PayloadCreationRequest): Response<ResponseBody>
}
