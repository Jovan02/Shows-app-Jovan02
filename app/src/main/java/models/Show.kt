package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Show(
    val id: String,
    val average_rating: Float,
    val description: String,
    val image_url: String,
    val no_of_reviews: Int = 0,
    val title: String
) : Parcelable