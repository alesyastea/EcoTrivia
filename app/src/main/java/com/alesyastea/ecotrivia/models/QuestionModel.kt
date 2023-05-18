package com.alesyastea.ecotrivia.models

import com.alesyastea.ecotrivia.utils.Constants.EMPTY_STRING
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class QuestionModel(
    @DocumentId
    var questionId: String = EMPTY_STRING,
    var question: String = EMPTY_STRING,
    var answer: String = EMPTY_STRING,
    var optionA: String = EMPTY_STRING,
    var optionB: String = EMPTY_STRING,
    var optionC: String = EMPTY_STRING,
    var timer: Long = 0
): Serializable