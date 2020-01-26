package com.rusmyhal.rates.feature.currencies.data

import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class CurrenciesRepository(private val currenciesApiService: CurrenciesApiService) {

    companion object {
        private const val FETCHING_INTERVAL_IN_MILLIS = 1000L
    }

    suspend fun fetchCurrenciesRates(baseCurrency: String? = null): Flow<List<CurrencyRate>> =
        flow {
            while (true) {
                emit(currenciesApiService.getCurrencies(baseCurrency).currenciesRates
                    .map { entry ->
                        CurrencyRate(entry.key, entry.value)
                    })
                delay(FETCHING_INTERVAL_IN_MILLIS)
            }
        }
}