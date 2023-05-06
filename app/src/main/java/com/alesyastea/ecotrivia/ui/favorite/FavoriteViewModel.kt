package com.alesyastea.ecotrivia.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alesyastea.ecotrivia.data.api.NewsRepository
import com.alesyastea.ecotrivia.models.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val repository: NewsRepository): ViewModel(){

    fun getSavedArticles() = repository.getFavoriteArticles()


    fun saveFavoriteArticle(article: NewsResponse.Article) = viewModelScope.launch (Dispatchers.IO) {
        repository.addToFavorite(article = article)
    }

    fun deleteArticle(article: NewsResponse.Article) = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteFromFavorite(article = article)
    }

}