package com.alesyastea.ecotrivia.ui.startQuiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentStartQuizBinding
import com.alesyastea.ecotrivia.ui.adapters.QuizAdapter

class StartQuizFragment : Fragment() {

    private var _binding: FragmentStartQuizBinding? = null
    private val mBinding get() = _binding!!
    private val viewModel by viewModels<StartQuizViewModel>()
    lateinit var quizAdapter: QuizAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartQuizBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        quizAdapter.setOnItemClickListener {
            val bundle = bundleOf("quiz" to it)
            view.findNavController().navigate(
                R.id.action_startQuizFragment_to_detailsQuizFragment,
                bundle
            )
        }

        viewModel.getLiveDataFromFireStore().observe(viewLifecycleOwner, Observer { quizModels ->
            quizAdapter.differ.submitList(quizModels)
        })
    }

    private fun hideProgressBar() {
        mBinding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar() {
        mBinding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false

    private fun initAdapter() {
        quizAdapter = QuizAdapter()
        mBinding.recyclerView.apply {
            adapter = quizAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
