package app.web.drjackycv.features.app.presentation

import kotlinx.serialization.Serializable

@Serializable
data class GameTwoModel(
    val text: String,
    val transcription: String,
    val first: String,
    val second: String,
    val third: String,
    val fourth: String,
    val correct: Int
)