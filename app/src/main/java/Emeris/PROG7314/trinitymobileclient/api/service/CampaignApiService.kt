package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.CampaignRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CampaignApiService {

    @GET("api/v1/campaigns")
    suspend fun getCampaigns(): Response<ResponseBody>

    @POST("api/v1/campaigns")
    suspend fun createCampaign(@Body body: CampaignRequest): Response<ResponseBody>

    @GET("api/v1/campaigns/{campaignID}")
    suspend fun getCampaign(@Path("campaignID") campaignId: Int): Response<ResponseBody>

    @PUT("api/v1/campaigns/{campaignID}")
    suspend fun updateCampaign(@Path("campaignID") campaignId: Int, @Body body: CampaignRequest): Response<ResponseBody>

    @DELETE("api/v1/campaigns/{campaignID}")
    suspend fun deleteCampaign(@Path("campaignID") campaignId: Int): Response<ResponseBody>

    @GET("api/v1/campaigns/{campaignID}/operators")
    suspend fun getCampaignOperators(@Path("campaignID") campaignId: Int): Response<ResponseBody>

    @POST("api/v1/campaigns/{campaignID}/operators/{operatorID}")
    suspend fun addOperatorToCampaign(
        @Path("campaignID") campaignId: Int,
        @Path("operatorID") operatorId: Int
    ): Response<ResponseBody>

    @DELETE("api/v1/campaigns/{campaignID}/operators/{operatorID}")
    suspend fun removeOperatorFromCampaign(
        @Path("campaignID") campaignId: Int,
        @Path("operatorID") operatorId: Int
    ): Response<ResponseBody>
}
