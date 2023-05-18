package com.alesyastea.ecotrivia.ui.resultQuiz

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentResultQuizBinding
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_LIST
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ResultQuizFragment : Fragment() {

    private var _binding: FragmentResultQuizBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: ResultQuizFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultQuizBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        FirebaseFirestore.getInstance().collection(QUIZ_LIST)
            .document(bundleArgs.quiz.quizId).collection("Results")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val correctAnswers = document.getLong("correct")
                        val incorrectAnswers = document.getLong("wrong")
                        val missedQuestions = document.getLong("missed")

                        if (correctAnswers != null && incorrectAnswers != null && missedQuestions != null) {
                            val total = correctAnswers + incorrectAnswers + missedQuestions
                            val percent = correctAnswers * 100 / total

                            mBinding.quizCorrectAnswers.text = correctAnswers.toString()
                            mBinding.quizWrongAnswers.text = incorrectAnswers.toString()
                            mBinding.quizMissedQuestions.text = missedQuestions.toString()
                            mBinding.tvPercent.text = "$percent%"
                            mBinding.progressBar.progress = percent.toInt()

                        }
                    }

                } else {
                    mBinding.tvPercent.text = task.exception?.toString()
                }
            }

        mBinding.btnHome.setOnClickListener {
            val bundle = bundleOf("quiz" to bundleArgs.quiz)
            view.findNavController().navigate(
                R.id.action_resultQuizFragment_to_startQuizFragment,
                bundle
            )
        }
    }
}