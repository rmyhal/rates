package com.rusmyhal.rates.feature.currencies.data

import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import com.rusmyhal.rates.util.test.OpenForTesting
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OpenForTesting
class CurrenciesRepository(private val currenciesApiService: CurrenciesApiService) {

    companion object {
        private const val FETCHING_INTERVAL_MILLIS = 1000L
    }

    suspend fun fetchCurrenciesRates(baseCurrencyCode: String? = null): Flow<List<CurrencyRate>> =
        flow {
            while (true) {
                emit(currenciesApiService.getCurrencies(baseCurrencyCode).currenciesRates
                    .map { entry ->
                        CurrencyRate(entry.key, entry.value)
                    })
                delay(FETCHING_INTERVAL_MILLIS)
            }
        }
}