package com.rusmyhal.rates.feature.currencies.data

import com.rusmyhal.rates.feature.currencies.data.entity.CurrenciesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrenciesApiService {

    @GET("latest")
    suspend fun getCurrencies(@Query("base") baseCurrency: String?): CurrenciesResponse
}