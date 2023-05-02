package com.alesyastea.ecotrivia.data.db

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alesyastea.ecotrivia.models.NewsResponse

interface ArticleDao {

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): LiveData<List<NewsResponse.Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: NewsResponse.Article)

    @Delete
    suspend fun delete(article: NewsResponse.Article)
}