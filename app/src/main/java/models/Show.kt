package models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(tableName = "show")
data class Show(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "average_rating") val average_rating: Float,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image_url") val image_url: String,
    @ColumnInfo(name = "no_of_reviews") val no_of_reviews: Int = 0,
    @ColumnInfo(name = "title") val title: String
) : Parcelable