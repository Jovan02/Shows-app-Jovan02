package models

data class Review(
    val ID: Int,
    var author: String,
    var comment: String,
    var ratingNum: Float,
)