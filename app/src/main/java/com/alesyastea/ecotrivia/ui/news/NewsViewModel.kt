package com.alesyastea.ecotrivia.ui.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alesyastea.ecotrivia.data.api.NewsRepository
import com.alesyastea.ecotrivia.models.NewsResponse
import com.alesyastea.ecotrivia.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    val newsLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var newsPage = 1

    init {
        if (Locale.getDefault().language == "ru") {
            getSearchNews("экология", "ru")
        } else {
            getSearchNews("ecology", "en")
        }
    }

    private fun getSearchNews(query: String, language: String) =
        viewModelScope.launch {
            newsLiveData.postValue(Resource.Loading())
            val response = repository.getSearchNews(query = query, language = language, pageNumber = newsPage)
            if (response.isSuccessful) {
                response.body().let { res ->
                    newsLiveData.postValue(Resource.Success(res))
                }
            } else {
                newsLiveData.postValue(Resource.Error(message = response.message()))
            }
        }
}