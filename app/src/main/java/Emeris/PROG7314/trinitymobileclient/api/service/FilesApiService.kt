package Emeris.PROG7314.trinitymobileclient.api.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FilesApiService {

    @GET("api/v1/files")
    suspend fun listFiles(@Query("folderName") folderName: Int? = null): Response<ResponseBody>

    @GET("api/v1/files/download/{fileName}")
    suspend fun downloadFile(
        @Path("fileName") fileName: String,
        @Query("folderName") folderName: Int? = null
    ): Response<ResponseBody>

    @Multipart
    @POST("api/v1/files")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<ResponseBody>
}
