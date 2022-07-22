package models

import androidx.annotation.DrawableRes

data class Show (
    val ID: Int,
    val name: String,
    val description: String,
    @DrawableRes val imageResourceId: Int
)

