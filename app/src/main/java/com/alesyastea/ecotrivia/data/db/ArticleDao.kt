package com.alesyastea.ecotrivia.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alesyastea.ecotrivia.models.NewsResponse

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<NewsResponse.Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: NewsResponse.Article)

    @Delete
    suspend fun delete(article: NewsResponse.Article)

    @Update
    suspend fun update(article: NewsResponse.Article)
}