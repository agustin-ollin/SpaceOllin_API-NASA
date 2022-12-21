package com.ollin.rocketollin.models

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getImageByLocalDate(@Url url: String): Response<NasaResponse>
}