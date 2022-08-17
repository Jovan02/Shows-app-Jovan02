package database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import models.Review
import models.ReviewEntity

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id IS :reviewId")
    fun getReview(reviewId: String): LiveData<ReviewEntity>

    @Query("SELECT * FROM review WHERE show_id IS :showId")
    fun getAllShowReviews(showId: String): LiveData<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReviews(review: List<ReviewEntity>)

}