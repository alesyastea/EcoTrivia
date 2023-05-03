package com.alesyastea.ecotrivia.ui.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentNewsBinding
import com.alesyastea.ecotrivia.ui.adapters.NewsAdapter
import com.alesyastea.ecotrivia.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<NewsViewModel>()
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_newsFragment_to_newsDetailsFragment,
                bundle
            )
        }

        viewModel.newsLiveData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Resource.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.e("CheckData", "!!!NewsFragment: error: ${it}")
                    }
                }
                is Resource.Loading -> {
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}