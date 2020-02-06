package com.rusmyhal.rates.feature.currencies.data

import com.rusmyhal.rates.core.Storage
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import com.rusmyhal.rates.util.test.OpenForTesting
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OpenForTesting
class CurrenciesRepository(
    private val currenciesApiService: CurrenciesApiService,
    private val localStorage: Storage
) {

    companion object {
        private const val FETCHING_INTERVAL_MILLIS = 1000L
        private const val KEY_SELECTED_CURRENCY_CODE = "currency_code"
    }

    private var cachedCurrencyCode: String? = null

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

    fun saveCurrencyCode(code: String) {
        cachedCurrencyCode = code
        localStorage.saveData(KEY_SELECTED_CURRENCY_CODE, code)
    }

    fun getLastSelectedCurrencyCode(): String? =
        cachedCurrencyCode ?: localStorage.getData(KEY_SELECTED_CURRENCY_CODE)
}