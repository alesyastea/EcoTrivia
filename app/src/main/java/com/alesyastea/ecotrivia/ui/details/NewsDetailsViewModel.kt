package com.alesyastea.ecotrivia.ui.details

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

    fun getSavedArticles() =
        repository.getFavoriteArticles()


    fun saveFavoriteArticle(article: NewsResponse.Article) = viewModelScope.launch (Dispatchers.IO) {
        repository.addToFavorite(article = article)
    }

    fun deleteArticle(article: NewsResponse.Article) = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteFromFavorite(article = article)
    }

}