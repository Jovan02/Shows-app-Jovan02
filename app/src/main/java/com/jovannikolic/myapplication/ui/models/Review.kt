package com.jovannikolic.myapplication.ui.models

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    var id: String,
    var comment: String,
    var rating: Int,
    var show_id: Int,
    var user: User
)