package com.alesyastea.ecotrivia.ui.search

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alesyastea.ecotrivia.data.api.NewsRepository
import com.alesyastea.ecotrivia.di.NewsApp
import com.alesyastea.ecotrivia.models.NewsResponse
import com.alesyastea.ecotrivia.utils.Constants.EMPTY_STRING
import com.alesyastea.ecotrivia.utils.Constants.ENGLISH_LANGUAGE
import com.alesyastea.ecotrivia.utils.Constants.RUSSIAN_LANGUAGE
import com.alesyastea.ecotrivia.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    app: Application,
    private val repository: NewsRepository
) : AndroidViewModel(app) {

    val searchNewsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null

    init {
        if (Locale.getDefault().language == RUSSIAN_LANGUAGE) {
            getSearchNews(EMPTY_STRING, RUSSIAN_LANGUAGE)
        } else {
            getSearchNews(EMPTY_STRING, ENGLISH_LANGUAGE)
        }
    }

    fun getSearchNews(query: String, language: String) =
        viewModelScope.launch {
            safeSearchNewsCall(query, language)
        }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body().let { res ->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = res
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = res!!.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: res)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchNewsCall(query: String, language: String) {
        newSearchQuery = query
        searchNewsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getSearchNews(query, language, searchNewsPage)
                searchNewsLiveData.postValue(handleSearchNewsResponse(response))
            } else {
                searchNewsLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNewsLiveData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
