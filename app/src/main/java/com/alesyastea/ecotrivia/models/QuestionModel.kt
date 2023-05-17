package com.alesyastea.ecotrivia.models

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class QuestionModel(
    @DocumentId
    var questionId: String = "",
    var question: String = "",
    var answer: String = "",
    var optionA: String = "",
    var optionB: String = "",
    var optionC: String = "",
    var timer: Long = 0
): Serializable