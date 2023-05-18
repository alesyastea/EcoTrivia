package com.alesyastea.ecotrivia.ui.resultQuiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import com.alesyastea.ecotrivia.utils.Constants.CORRECT_KEY
import com.alesyastea.ecotrivia.utils.Constants.MISSED_KEY
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_KEY
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_RESULTS
import com.alesyastea.ecotrivia.utils.Constants.WRONG_KEY

class ResultQuizFragment : Fragment() {

    private var _binding: FragmentResultQuizBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: ResultQuizFragmentArgs by navArgs()
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences =
            requireActivity().getSharedPreferences(QUIZ_RESULTS, Context.MODE_PRIVATE)

        val correctAnswers = sharedPreferences.getInt(CORRECT_KEY, 0)
        val incorrectAnswers = sharedPreferences.getInt(WRONG_KEY, 0)
        val missedQuestions = sharedPreferences.getInt(MISSED_KEY, 0)


        val total = correctAnswers + incorrectAnswers + missedQuestions
        val percent = correctAnswers * 100 / total

        mBinding.quizCorrectAnswers.text = correctAnswers.toString()
        mBinding.quizWrongAnswers.text = incorrectAnswers.toString()
        mBinding.quizMissedQuestions.text = missedQuestions.toString()
        mBinding.tvPercent.text = "$percent%"
        mBinding.progressBar.progress = percent

        mBinding.btnHome.setOnClickListener {
            val bundle = bundleOf(QUIZ_KEY to bundleArgs.quiz)
            view.findNavController().navigate(
                R.id.action_resultQuizFragment_to_startQuizFragment,
                bundle
            )
        }
    }
}