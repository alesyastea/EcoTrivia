package com.alesyastea.ecotrivia.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alesyastea.ecotrivia.data.api.NewsRepository
import com.alesyastea.ecotrivia.models.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(private val repository: NewsRepository): ViewModel(){

    init {
        getSavedArticles()
    }

    fun getSavedArticles() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("NewsDetailsViewModel", "!!! DB size: ${repository.getFavoriteArticles().size}")
        repository.getFavoriteArticles()
    }

    fun saveFavoriteArticle(article: NewsResponse.Article) = viewModelScope.launch (Dispatchers.IO) {
        repository.addToFavorite(article = article)
    }
}