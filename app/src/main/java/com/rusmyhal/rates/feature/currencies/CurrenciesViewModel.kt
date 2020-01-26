package com.rusmyhal.rates.feature.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusmyhal.rates.core.ResourcesManager
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@ExperimentalCoroutinesApi
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
    private val decimalFormat = DecimalFormat("#.##")

    private lateinit var currenciesJob: Job
    private var currentCurrencyRate = CurrencyRate(DEFAULT_CURRENCY_CODE, DEFAULT_CURRENCY_RATE)

    fun startUpdatingCurrencies() {
        currenciesJob = viewModelScope.launch(Dispatchers.Main) {
            currenciesRepository.fetchCurrenciesRates(currentCurrencyRate.currencyCode)
                .conflate()
                .collect {
                    _currencies.value = mapCurrenciesRates(it)
                }
        }
    }

    fun stopUpdatingCurrencies() {
        currenciesJob.cancel()
    }

    fun selectCurrency(currency: Currency) {
        stopUpdatingCurrencies()
        currentCurrencyRate = CurrencyRate(currency.code, DEFAULT_CURRENCY_RATE)
        startUpdatingCurrencies()
    }

    private fun mapCurrenciesRates(currenciesRates: List<CurrencyRate>): List<Currency> {
        return currenciesRates.toMutableList()
            .map { currencyRate ->
                Currency(
                    currencyRate.currencyCode,
                    decimalFormat.format(currencyRate.rate),
                    resourceManager.getCurrencyFlagResByCode(currencyRate.currencyCode)
                )
            }.toMutableList().apply {
                add(
                    0, Currency(
                        currentCurrencyRate.currencyCode,
                        currentCurrencyRate.rate.toString(),
                        resourceManager.getCurrencyFlagResByCode(currentCurrencyRate.currencyCode)
                    )
                )
            }
    }
}