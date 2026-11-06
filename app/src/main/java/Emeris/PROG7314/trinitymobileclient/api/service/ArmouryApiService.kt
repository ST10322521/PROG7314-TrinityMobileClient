package Emeris.PROG7314.trinitymobileclient.api.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ArmouryApiService {

    @GET("api/v1/armoury/tools")
    suspend fun getTools(
        @Query("pagination") pagination: Int? = null,
        @Query("rows") rows: Int? = null
    ): Response<ResponseBody>

    @Multipart
    @POST("api/v1/armoury/upload")
    suspend fun uploadTool(
        @Part("Name") name: RequestBody,
        @Part("OperatorID") operatorId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @DELETE("api/v1/armoury/remove/{toolID}")
    suspend fun removeTool(@Path("toolID") toolId: Int): Response<ResponseBody>
}
