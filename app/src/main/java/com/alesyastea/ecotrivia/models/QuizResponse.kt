package com.alesyastea.ecotrivia.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class QuizModel(
    @DocumentId
    val quizId: String = "",
    val image: String = "",
    val quizname: String = "",
    val visibility: String = "",
    val level: String = "",
    val description: String = "",
    val question: Long = 0
): Serializable