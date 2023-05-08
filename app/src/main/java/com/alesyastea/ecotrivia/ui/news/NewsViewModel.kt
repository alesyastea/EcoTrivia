package com.alesyastea.ecotrivia.ui.news

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alesyastea.ecotrivia.data.api.NewsRepository
import com.alesyastea.ecotrivia.di.NewsApp
import com.alesyastea.ecotrivia.models.NewsResponse
import com.alesyastea.ecotrivia.utils.Constants.ENGLISH_LANGUAGE
import com.alesyastea.ecotrivia.utils.Constants.ENGLISH_REQUEST
import com.alesyastea.ecotrivia.utils.Constants.RUSSIAN_LANGUAGE
import com.alesyastea.ecotrivia.utils.Constants.RUSSIAN_REQUEST
import com.alesyastea.ecotrivia.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(app: Application, private val repository: NewsRepository) :
    AndroidViewModel(app) {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsPage = 1
    var newsResponse: NewsResponse? = null

    init {
        if (Locale.getDefault().language == RUSSIAN_LANGUAGE) {
            getSearchNews(RUSSIAN_REQUEST, RUSSIAN_LANGUAGE)
        } else {
            getSearchNews(ENGLISH_REQUEST, ENGLISH_LANGUAGE)
        }
    }

    fun getSearchNews(query: String, language: String) =
        viewModelScope.launch {
            safeNewsCall(query, language)
        }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body().let { res ->
                newsPage++
                if (newsResponse == null) {
                    newsResponse = res
                } else {
                    val oldArticles = newsResponse?.articles
                    val newArticles = res!!.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(newsResponse ?: res)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeNewsCall(query: String, language: String) {
        newsLiveData.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response =
                    repository.getSearchNews(
                        query = query,
                        language = language,
                        pageNumber = newsPage
                    )
                newsLiveData.postValue(handleNewsResponse(response))
            } else {
                newsLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> newsLiveData.postValue(Resource.Error("Network Failure"))
                else -> newsLiveData.postValue(Resource.Error("Conversion Error"))
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
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}