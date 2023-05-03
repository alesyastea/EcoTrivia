package com.alesyastea.ecotrivia.di

import android.content.Context
import androidx.room.Room
import com.alesyastea.ecotrivia.data.api.NewsService
import com.alesyastea.ecotrivia.data.db.ArticleDao
import com.alesyastea.ecotrivia.data.db.ArticleDatabase
import com.alesyastea.ecotrivia.utils.Constants.BASE_URL
import com.alesyastea.ecotrivia.utils.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class )
object AppModule {

    @Provides
    fun baseUrl() = BASE_URL

    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC)

    @Provides
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): NewsService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient())
            .build()
            .create(NewsService::class.java)

    @Provides
    @Singleton
    fun provideArticleDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ArticleDatabase::class.java,
            DB_NAME
        ).build()

    @Provides
    fun provideArticleDao(appDatabase: ArticleDatabase): ArticleDao {
        return appDatabase.getArticleDao()
    }
}