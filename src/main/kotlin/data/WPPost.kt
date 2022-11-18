package data

import kotlinx.serialization.Serializable

@Serializable
data class WPPost(
    val title : String,
    val content : String?,
    val status : String,
    val categories : Int,
    val featured_media : Int
)