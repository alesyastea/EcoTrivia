package com.alesyastea.ecotrivia.ui.startQuiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alesyastea.ecotrivia.data.api.QuizRepository
import com.alesyastea.ecotrivia.models.QuizModel

class StartQuizViewModel: ViewModel(), QuizRepository.OnFireStoreDataAdded {

    var startQuizLiveData: MutableLiveData<List<QuizModel>> = MutableLiveData()
    var quizRepository = QuizRepository(this)

    init {
        quizRepository.getDataFromFireStore()
    }

    fun getLiveDataFromFireStore(): MutableLiveData<List<QuizModel>> {
        return startQuizLiveData
    }

    override fun quizDataAdded(quizModelList: List<QuizModel>) {
        startQuizLiveData.value = quizModelList
    }

    override fun onError(exception: Exception?) {
    }
}