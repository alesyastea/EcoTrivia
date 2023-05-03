package com.alesyastea.ecotrivia.data.api

import com.alesyastea.ecotrivia.models.NewsResponse
import com.alesyastea.ecotrivia.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("/v2/everything")
    suspend fun getAll (
        @Query("q") query: String = "ecology",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>

}