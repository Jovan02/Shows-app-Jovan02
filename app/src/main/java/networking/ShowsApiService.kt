package networking

import androidx.annotation.NonNull
import com.jovannikolic.myapplication.ui.models.AddReviewRequest
import com.jovannikolic.myapplication.ui.models.AddReviewResponse
import com.jovannikolic.myapplication.ui.models.GetReviewsResponse
import com.jovannikolic.myapplication.ui.models.LoginRequest
import com.jovannikolic.myapplication.ui.models.LoginResponse
import com.jovannikolic.myapplication.ui.models.RegisterRequest
import com.jovannikolic.myapplication.ui.models.RegisterResponse
import com.jovannikolic.myapplication.ui.models.ShowDetailsResponse
import com.jovannikolic.myapplication.ui.models.ShowsListResponse
import com.jovannikolic.myapplication.ui.models.UpdatePhotoResponse
import com.jovannikolic.myapplication.ui.models.UserDataResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowsApiService {

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @PUT("/users")
    fun updatePhoto(@Part("email") email: String, @Part requestImage: MultipartBody.Part): Call<UpdatePhotoResponse>

    @GET("/users/me")
    fun userData(): Call<UserDataResponse>

    @GET("/shows")
    fun showsList(@NonNull @Query("page") page: String, @NonNull @Query("items") items: String): Call<ShowsListResponse>

    @GET("/shows/{id}")
    fun getShowDetails(@Path("id") id: String): Call<ShowDetailsResponse>

    @GET("/shows/{show_id}/reviews")
    fun getReviews(@Path("show_id") show_id: String): Call<GetReviewsResponse>

    @POST("/reviews")
    fun addReview(@Body request: AddReviewRequest): Call<AddReviewResponse>

}