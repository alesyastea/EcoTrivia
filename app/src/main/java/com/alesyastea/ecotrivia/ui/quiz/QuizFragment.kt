package com.alesyastea.ecotrivia.ui.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentQuizBinding
import com.alesyastea.ecotrivia.models.QuestionModel
import com.alesyastea.ecotrivia.utils.Constants.QUESTIONS
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_LIST
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: QuizFragmentArgs by navArgs()
    private val allQuestionList: MutableList<QuestionModel> = mutableListOf()
    private val questionToAnswer: MutableList<QuestionModel> = mutableListOf()
    private var correctAnswer: Int = 0
    private var wrongAnswer: Int = 0
    private var missedAnswer: Int = 0
    private var currentQuestion: Int = 0
    private var canAnswer: Boolean = false
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val quizArgs = bundleArgs.quiz
        navController = Navigation.findNavController(view)

        mBinding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }


        FirebaseFirestore.getInstance().collection(QUIZ_LIST)
            .document(quizArgs.quizId).collection(QUESTIONS).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    allQuestionList.addAll(task.result?.toObjects(QuestionModel::class.java)!!)
                    pickedQuestion()
                    loadUi()
                } else {
                    mBinding.tvQuestion.text = task.exception.toString()
                }
            }

        mBinding.btnOptionA.setOnClickListener {
            verifyAnswer(mBinding.btnOptionA)
        }
        mBinding.btnOptionB.setOnClickListener {
            verifyAnswer(mBinding.btnOptionB)
        }
        mBinding.btnOptionC.setOnClickListener {
            verifyAnswer(mBinding.btnOptionC)
        }
        mBinding.btnNext.setOnClickListener {
            if (currentQuestion == quizArgs.question.toInt()) {
                submitResult()
            } else {
                currentQuestion++
                loadQuestion(currentQuestion)
                resetOptions()
            }
        }
    }

    private fun pickedQuestion() {
        val maxQuestions = bundleArgs.quiz.question.coerceAtMost(allQuestionList.size.toLong())
        for (i in 0 until maxQuestions) {
            val randomNumber = getRandomInt(0, allQuestionList.size)
            questionToAnswer.add(allQuestionList[randomNumber])
            allQuestionList.removeAt(randomNumber)
        }
    }

    private fun getRandomInt(min: Int, max: Int): Int {
        return Random().nextInt(max - min) + min
    }

    private fun loadUi() {
        mBinding.tvLoadingQuiz.text = getString(R.string.loading_question)
        mBinding.tvLoadingQuiz.text = bundleArgs.quiz.quizname
        enableOptions()
        loadQuestion(1)
    }

    @SuppressLint("SetTextI18n")
    private fun loadQuestion(questNum: Int) {
        mBinding.tvQuestion.text = "$questNum. ${questionToAnswer[questNum - 1].question}"
        mBinding.btnOptionA.text = questionToAnswer[questNum - 1].optionA
        mBinding.btnOptionB.text = questionToAnswer[questNum - 1].optionB
        mBinding.btnOptionC.text = questionToAnswer[questNum - 1].optionC
        canAnswer = true
        currentQuestion = questNum
        startTimer(questNum)
    }

    private fun startTimer(questNum: Int) {
        val timeToAnswer = questionToAnswer[questNum - 1].timer
        mBinding.tvTimer.text = timeToAnswer.toString()
        mBinding.progressBar.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(timeToAnswer * 1000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.tvTimer.text = (millisUntilFinished / 1000).toString()
                val percent = millisUntilFinished / (timeToAnswer * 10)
                mBinding.progressBar.progress = percent.toInt()
            }

            override fun onFinish() {
                canAnswer = false
                mBinding.tvFeedback.text = getString(R.string.time_is_up)
                mBinding.tvFeedback.setTextColor(resources.getColor(R.color.flamingo, null))
                missedAnswer++
                showNextButton()
            }
        }
        countDownTimer.start()
    }

    private fun enableOptions() {
        mBinding.btnOptionA.visibility = View.VISIBLE
        mBinding.btnOptionB.visibility = View.VISIBLE
        mBinding.btnOptionC.visibility = View.VISIBLE
        mBinding.btnNext.visibility = View.INVISIBLE
        mBinding.btnOptionA.isEnabled = true
        mBinding.btnOptionB.isEnabled = true
        mBinding.btnOptionC.isEnabled = true
        mBinding.btnNext.isEnabled = false
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun resetOptions() {
        mBinding.btnOptionA.background = resources.getDrawable(R.drawable.outline_button_bg, null)
        mBinding.btnOptionB.background = resources.getDrawable(R.drawable.outline_button_bg, null)
        mBinding.btnOptionC.background = resources.getDrawable(R.drawable.outline_button_bg, null)
        mBinding.btnOptionA.setTextColor(resources.getColor(R.color.white, null))
        mBinding.btnOptionB.setTextColor(resources.getColor(R.color.white, null))
        mBinding.btnOptionC.setTextColor(resources.getColor(R.color.white, null))
        mBinding.tvFeedback.visibility = View.INVISIBLE
        mBinding.btnNext.visibility = View.INVISIBLE
        mBinding.btnNext.isEnabled = false
    }

    private fun verifyAnswer(selectedButton: Button) {
        if (canAnswer) {
            selectedButton.setTextColor(resources.getColor(R.color.white, null))
            selectedButton.setBackgroundResource(R.drawable.primary_button_bg)
            if (questionToAnswer[currentQuestion - 1].answer == selectedButton.text) {
                correctAnswer++
                selectedButton.setBackgroundColor(resources.getColor(R.color.woodland, null))
                mBinding.tvFeedback.text = getString(R.string.correct_answer)
                mBinding.tvFeedback.setTextColor(resources.getColor(R.color.white, null))
            } else {
                selectedButton.setBackgroundColor(resources.getColor(R.color.flamingo, null))
                wrongAnswer++
                mBinding.tvFeedback.text = getString(R.string.incorrect_ancwer)
                mBinding.tvFeedback.setTextColor(resources.getColor(R.color.cowboy, null))
            }
            canAnswer = false
            countDownTimer.cancel()
            showNextButton()
        }
    }

    private fun submitResult() {
        val hashMap = HashMap<String, Any>()

        hashMap["correct"] = correctAnswer
        hashMap["wrong"] = wrongAnswer
        hashMap["missed"] = missedAnswer

        FirebaseFirestore.getInstance().collection("QuizList")
            .document(bundleArgs.quiz.quizId)
            .collection("Results")
            .document()
            .set(hashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val bundle = bundleOf("quiz" to bundleArgs.quiz)
                    navController.navigate(R.id.action_quizFragment_to_resultQuizFragment, bundle)
                } else {
                    mBinding.tvFeedback.text = task.exception.toString()
                }
            }
    }

    private fun showNextButton() {
        if (currentQuestion == bundleArgs.quiz.question.toInt()) {
            mBinding.btnNext.text = getString(R.string.go_to_results)
        }
        mBinding.tvFeedback.visibility = View.VISIBLE
        mBinding.btnNext.visibility = View.VISIBLE
        mBinding.btnNext.isEnabled = true
    }
}