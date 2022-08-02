package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import models.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id IS :reviewId")
    fun getReview(reviewId: String): LiveData<Review>

    @Query("SELECT * FROM review WHERE show_id IS :showId")
    fun getAllShowReviews(showId: String): LiveData<List<Review>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReview(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReviews(): LiveData<List<Review>>

}