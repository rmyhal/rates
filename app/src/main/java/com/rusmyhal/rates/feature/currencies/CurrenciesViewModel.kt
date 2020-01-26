package com.rusmyhal.rates.feature.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusmyhal.rates.core.ResourcesManager
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import java.text.DecimalFormat
import java.util.Currency as JavaCurrency

class CurrenciesViewModel(
    private val currenciesRepository: CurrenciesRepository,
    private val resourceManager: ResourcesManager
) : ViewModel() {

    companion object {
        private const val DEFAULT_CURRENCY_CODE = "EUR"
        private const val DEFAULT_CURRENCY_RATE = 1f
    }

    val currencies: LiveData<List<Currency>>
        get() = _currencies

    private val _currencies = MutableLiveData<List<Currency>>()
    private val decimalFormat = DecimalFormat("0.00")

    private lateinit var currenciesJob: Job
    private var currentCurrencyRate = CurrencyRate(DEFAULT_CURRENCY_CODE, DEFAULT_CURRENCY_RATE)

    @ExperimentalCoroutinesApi
    fun startUpdatingCurrencies() {
        currenciesJob = viewModelScope.launch(Dispatchers.Main) {
            currenciesRepository.fetchCurrenciesRates(currentCurrencyRate.currencyCode)
                .conflate()
                .collect {
                    withContext(Dispatchers.IO) {
                        _currencies.postValue(mapCurrenciesRates(it))
                    }
                }
        }
    }

    fun stopUpdatingCurrencies() {
        currenciesJob.cancel()
    }

    private fun mapCurrenciesRates(currenciesRates: List<CurrencyRate>): List<Currency> {
        val rates = currenciesRates.toMutableList()
            .apply { add(0, currentCurrencyRate) }

        return rates.map { currencyRate ->
            val javaCurrency = JavaCurrency.getInstance(currencyRate.currencyCode)
            Currency(
                currencyRate.currencyCode,
                javaCurrency.displayName,
                decimalFormat.format(currencyRate.rate),
                resourceManager.getCurrencyFlagResByCode(currencyRate.currencyCode)
            )
        }
    }
}