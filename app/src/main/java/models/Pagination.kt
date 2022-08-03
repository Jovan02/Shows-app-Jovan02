package models

import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    val count: Int,
    val page: Int,
    val items: Int,
    val pages: Int
)