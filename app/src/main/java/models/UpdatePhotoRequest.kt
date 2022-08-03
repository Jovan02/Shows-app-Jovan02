package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePhotoRequest(
    @SerialName("email") val email: String
)