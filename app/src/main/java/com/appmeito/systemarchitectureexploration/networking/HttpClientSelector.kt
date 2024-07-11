package com.appmeito.systemarchitectureexploration.networking

import com.appmeito.systemarchitectureexploration.networking.http11.HTTP11ClientHttpURLConnection
import com.appmeito.systemarchitectureexploration.networking.retrofit.RetrofitClient
import okhttp3.Protocol

object HttpClientSelector {
    private val http11ClientHttpURLConnection= HTTP11ClientHttpURLConnection()
    private val http11ClientOkHttpClient= OkHttpHTTPClient(listOf( Protocol.HTTP_1_1))
    private val http11ClientRetrofitClient= RetrofitClient(listOf( Protocol.HTTP_1_1))
    private val httP20ClientOkHttpClient= OkHttpHTTPClient(listOf( Protocol.HTTP_1_1,Protocol.HTTP_2))
    private val httP30ClientOkHttpClient= OkHttpHTTPClient(listOf( Protocol.QUIC))

    fun getClient(type:HTTPTYPES):NetworkInterface{
        return when(type){
            HTTPTYPES.HTTP11_HTTPURLCONNECTION-> http11ClientHttpURLConnection
            HTTPTYPES.HTTP11_OKHTTP-> http11ClientOkHttpClient
            HTTPTYPES.HTTP11_RETROFIT-> http11ClientRetrofitClient
            HTTPTYPES.HTTP20_OKHTTP-> httP20ClientOkHttpClient
            HTTPTYPES.HTTP30_OKHTTP-> httP30ClientOkHttpClient
        }
    }
}
enum class HTTPTYPES{
    HTTP11_HTTPURLCONNECTION,
    HTTP11_OKHTTP,
    HTTP11_RETROFIT,
    HTTP20_OKHTTP,
    HTTP30_OKHTTP
}