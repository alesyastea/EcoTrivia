package com.alesyastea.ecotrivia.data.api

import com.alesyastea.ecotrivia.data.db.ArticleDao
import com.alesyastea.ecotrivia.models.NewsResponse
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsService: NewsService,
    private val articleDao: ArticleDao
) {

    suspend fun getSearchNews(query: String, language: String, pageNumber: Int) =
        newsService.getAll(query = query, language = language, page = pageNumber)

    fun getFavoriteArticles() = articleDao.getAllArticles()

    suspend fun addToFavorite(article: NewsResponse.Article){
        article.isFavorite = true
        articleDao.insert(article = article)
    }

    suspend fun deleteFromFavorite(article: NewsResponse.Article) {
        article.isFavorite = false
        articleDao.delete(article = article)
    }
}