package models

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val pagination: Pagination
)