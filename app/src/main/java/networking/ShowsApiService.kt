package networking

import models.GetReviewsResponse
import models.LoginRequest
import models.LoginResponse
import models.RegisterRequest
import models.RegisterResponse
import models.ShowDetailsResponse
import models.ShowsListResponse
import models.UpdatePhotoRequest
import models.UpdatePhotoResponse
import models.UserDataResponse
import okhttp3.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PUT("/users")
    fun updatePhoto(@Body request: Request): Call<UpdatePhotoResponse>

    @GET("/users/me")
    fun userData(): Call<UserDataResponse>

    @GET("/shows")
    fun showsList(): Call<ShowsListResponse>

    @GET("/shows/{id}")
    fun getShowDetails(@Path("id") id: String): Call<ShowDetailsResponse>

    @GET("/shows/{show_id}/reviews")
    fun getReviews(@Path("show_id") show_id: String): Call<GetReviewsResponse>

}