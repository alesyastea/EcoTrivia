package com.alesyastea.ecotrivia.data.api

import com.alesyastea.ecotrivia.models.QuizModel
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_LIST
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuizRepository(private val fireStoreDataAdded: OnFireStoreDataAdded) {

    private val fireStore = FirebaseFirestore.getInstance()
    private val quizRef: Query = fireStore.collection(QUIZ_LIST)

    fun getDataFromFireStore() {
        quizRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val quizModelList = task.result?.toObjects(QuizModel::class.java)
                if (quizModelList != null) {
                    fireStoreDataAdded.quizDataAdded(quizModelList)
                }
            } else {
                fireStoreDataAdded.onError(task.exception)
            }
        }
    }

    interface OnFireStoreDataAdded {
        fun quizDataAdded(quizModelList: List<QuizModel>)
        fun onError(exception: Exception?)
    }
}