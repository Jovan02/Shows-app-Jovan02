package models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "review")
data class ReviewEntity(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "show_id") var show_id: Int,
    @ColumnInfo(name = "user_id") var user_id: String,
    @ColumnInfo(name = "user_email") var user_email: String,
    @ColumnInfo(name = "user_image_url") var user_image_url: String?

)