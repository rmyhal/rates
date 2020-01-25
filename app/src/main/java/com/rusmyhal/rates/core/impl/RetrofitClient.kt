package com.rusmyhal.rates.core.impl

import com.rusmyhal.rates.BuildConfig
import com.rusmyhal.rates.core.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient : NetworkClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.ENDPOINT)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}