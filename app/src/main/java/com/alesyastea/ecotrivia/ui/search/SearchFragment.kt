package com.alesyastea.ecotrivia.ui.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alesyastea.ecotrivia.R
import com.alesyastea.ecotrivia.databinding.FragmentSearchBinding
import com.alesyastea.ecotrivia.ui.adapters.NewsAdapter
import com.alesyastea.ecotrivia.utils.Constants
import com.alesyastea.ecotrivia.utils.Constants.ENGLISH_LANGUAGE
import com.alesyastea.ecotrivia.utils.Constants.RUSSIAN_LANGUAGE
import com.alesyastea.ecotrivia.utils.Resource
import com.google.firebase.analytics.FirebaseAnalytics
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
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(requireContext()) }
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
                        if(Locale.getDefault().language == RUSSIAN_LANGUAGE) {
                            viewModel.getSearchNews(query = it.toString(), language = RUSSIAN_LANGUAGE)
                        } else {
                            viewModel.getSearchNews(query = it.toString(), language = ENGLISH_LANGUAGE)
                        }
                        firebaseAnalytics.logEvent("search", bundleOf("query" to text.toString()))
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
            firebaseAnalytics.logEvent("click_search_news", bundleOf("article_title" to it.title))
        }


        viewModel.searchNewsLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage) {
                            mBinding.recyclerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.e("SearchNewsFragment", "!!! An error occured: $it")
                        showErrorMessage()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        mBinding.btnRetry.setOnClickListener {
            if (mBinding.etSearch.text.toString().isNotEmpty()) {
                viewModel.getSearchNews(mBinding.etSearch.text.toString(), ENGLISH_LANGUAGE)
            } else {
                hideErrorMessage()
            }
        }
    }

    private fun hideProgressBar() {
        mBinding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar() {
        firebaseAnalytics.logEvent("load_news_list", null)
        mBinding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        mBinding.noInternetLayout.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage() {
        mBinding.noInternetLayout.visibility = View.VISIBLE
        isError = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                if(Locale.getDefault().language == RUSSIAN_LANGUAGE) {
                    viewModel.getSearchNews(mBinding.etSearch.text.toString(), RUSSIAN_LANGUAGE)
                } else {
                    viewModel.getSearchNews(mBinding.etSearch.text.toString(), ENGLISH_LANGUAGE)
                }
                isScrolling = false
            }
        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
    private fun initAdapter() {
        newsAdapter = NewsAdapter()
        mBinding.recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
}