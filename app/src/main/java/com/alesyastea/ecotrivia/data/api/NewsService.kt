package com.alesyastea.ecotrivia.data.api

import com.alesyastea.ecotrivia.utils.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("/v2/everything")
    suspend fun getAll (
        @Query("q") query: String,
        @Query("searchIn") searchIn: String,
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    )

    suspend fun getHeadlines(
        @Query("q") query: String = "ecology",
        @Query("searchIn") searchIn: String = "title,description",
        @Query("country") country: String = "pl",
        @Query("page") page: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    )
}