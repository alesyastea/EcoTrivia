package com.alesyastea.ecotrivia.ui.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentSearchBinding
import com.alesyastea.ecotrivia.ui.adapters.NewsAdapter
import com.alesyastea.ecotrivia.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val mBinding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        var job: Job? = null

        mBinding.etSearch.addTextChangedListener{ text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                text?.let {
                    if(it.toString().isNotEmpty()) {
                        if(Locale.getDefault().language == "ru") {
                            viewModel.getSearchNews(query = it.toString(), language = "ru")
                        } else {
                            viewModel.getSearchNews(query = it.toString(), language = "en")
                        }
                    }
                }
            }
        }

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_searchFragment_to_newsDetailsFragment,
                bundle
            )
        }


        viewModel.searchNewsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error -> {
                    mBinding.progressBar.visibility = View.INVISIBLE
                    response.data?.let {
                        Log.e("CheckData", "!!!SearchNewsFragment: error: ${it}")
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