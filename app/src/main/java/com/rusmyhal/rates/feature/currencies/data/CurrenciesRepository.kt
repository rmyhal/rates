package com.rusmyhal.rates.feature.currencies.data

class CurrenciesRepository(private val currenciesApiService: CurrenciesApiService) {

    suspend fun fetchCurrencies(baseCurrency: String? = null) =
        currenciesApiService.getCurrencies(baseCurrency)
}