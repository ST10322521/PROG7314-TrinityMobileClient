package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.OperatorRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OperatorApiService {

    @GET("api/v1/operators")
    suspend fun getOperators(): Response<ResponseBody>

    @POST("api/v1/operators")
    suspend fun createOperator(@Body body: OperatorRequest): Response<ResponseBody>

    @GET("api/v1/operators/{operatorID}")
    suspend fun getOperator(@Path("operatorID") operatorId: Int): Response<ResponseBody>

    @PUT("api/v1/operators/{operatorID}")
    suspend fun updateOperator(@Path("operatorID") operatorId: Int, @Body body: OperatorRequest): Response<ResponseBody>

    @DELETE("api/v1/operators/{operatorID}")
    suspend fun deleteOperator(@Path("operatorID") operatorId: Int): Response<ResponseBody>
}
