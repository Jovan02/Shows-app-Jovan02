package networking

import models.LoginRequest
import models.LoginResponse
import models.RegisterRequest
import models.RegisterResponse
import models.UpdatePhotoRequest
import models.UpdatePhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PUT("/users")
    fun updatePhoto(@Body request: MultipartBody.Part, create: RequestBody): Call<UpdatePhotoResponse>


}