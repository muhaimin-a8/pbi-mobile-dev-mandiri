package me.muhaimin.mandirinews.data.remote.retrofit

import me.muhaimin.mandirinews.data.remote.response.GetNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getHeadlineNews(
        @Query("q") query: String? = null,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): GetNewsResponse

    @GET("everything")
    suspend fun getEverythingNews(
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): GetNewsResponse
}