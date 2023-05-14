package com.alesyastea.ecotrivia.models

import com.google.firebase.firestore.DocumentId

data class QuizModel(
    @DocumentId val quizId: String = "",
    val image: String = "",
    val quizname: String = "",
    val visibility: String = "",
    val level: String = "",
    val description: String = "",
    val question: Long = 0
)