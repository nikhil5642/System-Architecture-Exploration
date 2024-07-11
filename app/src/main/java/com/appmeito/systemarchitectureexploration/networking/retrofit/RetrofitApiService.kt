package com.appmeito.systemarchitectureexploration.networking.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Url

interface RetrofitApiService {
    @GET
    fun get(@Url url: String): Call<JsonObject>

    @POST
    fun post(@Url url: String, @Body data: String): Call<JsonObject>
}