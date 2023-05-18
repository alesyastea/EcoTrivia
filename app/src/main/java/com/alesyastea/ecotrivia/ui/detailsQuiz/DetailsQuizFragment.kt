package com.alesyastea.ecotrivia.ui.detailsQuiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentDetailsQuizBinding
import com.alesyastea.ecotrivia.utils.Constants.QUIZ_KEY
import com.bumptech.glide.Glide


class DetailsQuizFragment : Fragment() {

    private var _binding: FragmentDetailsQuizBinding? = null
    private val mBinding get() = _binding!!
    private val bundleArgs: DetailsQuizFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsQuizBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val quizArgs = bundleArgs.quiz

        quizArgs.let { quiz ->
            quiz.image.let {
                Glide.with(requireContext())
                .load(quiz.image)
                .placeholder(R.drawable.quiz_placeholder)
                .centerCrop()
                .into(mBinding.headerImage)
            }
            mBinding.headerImage.clipToOutline = true
            mBinding.quizTitle.text = quiz.quizname
            mBinding.quizDescription.text = quiz.description
            mBinding.quizLevel.text = quiz.level
            mBinding.quizQuestions.text = quiz.question.toString()
        }

        mBinding.btnTakeQuiz.setOnClickListener {
            val bundle = bundleOf(QUIZ_KEY to quizArgs)
            view.findNavController().navigate(
                R.id.action_detailsQuizFragment_to_quizFragment,
                bundle
            )
        }

        mBinding.icBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}