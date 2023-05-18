package com.alesyastea.ecotrivia.models

import com.alesyastea.ecotrivia.utils.Constants.EMPTY_STRING
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class QuizModel(
    @DocumentId
    val quizId: String = EMPTY_STRING,
    val image: String = EMPTY_STRING,
    val quizname: String = EMPTY_STRING,
    val visibility: String = EMPTY_STRING,
    val level: String = EMPTY_STRING,
    val description: String = EMPTY_STRING,
    val question: Long = 0
): Serializable