package models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "review")
data class Review(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "rating") var rating: Int,
    @ColumnInfo(name = "show_id") var show_id: Int,
    @ColumnInfo(name = "user") var user: User
)