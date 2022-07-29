package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowsListResponse(
   @SerialName("shows") val shows: List<Show>,
   @SerialName("meta") val meta: Meta
)