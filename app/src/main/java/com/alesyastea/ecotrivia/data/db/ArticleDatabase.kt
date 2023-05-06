package com.alesyastea.ecotrivia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alesyastea.ecotrivia.models.NewsResponse

@Database(entities = [NewsResponse.Article::class], version = 1, exportSchema = true)
abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

}