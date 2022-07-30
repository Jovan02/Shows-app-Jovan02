package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddReviewResponse(
    @SerialName("review") val review: Review
)