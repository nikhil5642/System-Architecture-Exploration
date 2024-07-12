package com.appmeito.systemarchitectureexploration.networking

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.appmeito.systemarchitectureexploration.GetUserQuery
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class GraphQLClient(private val okHttpClient: OkHttpClient,private val url:String) {
    private val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(url)
        .okHttpClient(okHttpClient)
        .build()

    suspend fun getUser(id: Int): GetUserQuery.User? {
        val response = apolloClient.query(GetUserQuery(id)).execute()
        return response.data?.user
    }
}
