package com.rusmyhal.rates.feature.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusmyhal.rates.R
import com.rusmyhal.rates.core.ResourcesManager
import com.rusmyhal.rates.core.Schedulers
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.text.DecimalFormat

@ExperimentalCoroutinesApi
class CurrenciesViewModel(
    private val currenciesRepository: CurrenciesRepository,
    private val resourceManager: ResourcesManager,
    private val schedulers: Schedulers
) : ViewModel() {

    companion object {
        private const val DEFAULT_CURRENCY_CODE = "EUR"
        private const val DEFAULT_CURRENCY_RATE = 1f
        private const val DEFAULT_AMOUNT_FORMAT_PATTERN = "0.00"
        private const val BASE_CURRENCY_POSITION = 0
        private const val CURRENCIES_FETCHING_RETRY_DELAY_MILLIS = 1000L
    }

    val currencies: LiveData<List<Currency>>
        get() = _currencies
    private val _currencies = MutableLiveData<List<Currency>>()

    val networkErrorMessage: LiveData<String?>
        get() = _networkErrorMessage
    private val _networkErrorMessage = MutableLiveData<String?>()

    private val rateFormat = DecimalFormat(DEFAULT_AMOUNT_FORMAT_PATTERN)

    private lateinit var currenciesJob: Job
    private var currenciesRates: List<CurrencyRate> = emptyList()
    private var baseCurrencyRate = CurrencyRate(DEFAULT_CURRENCY_CODE, DEFAULT_CURRENCY_RATE)
    private var currentAmount: Float = baseCurrencyRate.rate

    fun startUpdatingCurrencies() {
        currenciesJob = viewModelScope.launch(schedulers.main) {
            currenciesRepository.fetchCurrenciesRates(baseCurrencyRate.code)
                .retryWhen { cause, _ ->
                    // retry only when IOException
                    if (cause is IOException) {
                        _networkErrorMessage.value =
                            resourceManager.getString(R.string.currencies_network_error)
                        delay(CURRENCIES_FETCHING_RETRY_DELAY_MILLIS)
                        true
                    } else {
                        false
                    }
                }
                .conflate()
                .collect { newRates ->
                    currenciesRates = newRates
                    withContext(schedulers.default) {
                        _currencies.postValue(mapCurrenciesRates(newRates))
                    }

                    if (_networkErrorMessage.value != null) {
                        _networkErrorMessage.value = null
                    }
                }
        }
    }

    fun stopUpdatingCurrencies() {
        currenciesJob.cancel()
    }

    fun selectCurrency(currency: Currency) {
        if (currency.code == baseCurrencyRate.code) return

        stopUpdatingCurrencies()
        baseCurrencyRate = CurrencyRate(currency.code, currency.amount.toFloatOrNull() ?: 0f)
        currentAmount = baseCurrencyRate.rate
        startUpdatingCurrencies()
    }

    fun onAmountChanged(newAmount: String) {
        currentAmount = newAmount.toFloatOrNull() ?: 0f
        baseCurrencyRate.rate = currentAmount

        _currencies.value = mapCurrenciesRates(currenciesRates)
    }

    private fun mapCurrenciesRates(rates: List<CurrencyRate>): List<Currency> {
        if (rates.isEmpty()) return emptyList()
        return rates
            .map { currencyRate ->
                Currency(
                    currencyRate.code,
                    calculateConvertingRate(currentAmount, currencyRate.rate),
                    resourceManager.getCurrencyFlagResByCode(currencyRate.code)
                )
            }.toMutableList().apply {
                addBaseCurrency(this)
            }
    }

    private fun addBaseCurrency(currencies: MutableList<Currency>) {
        val baseCurrencyAmount =
            if (baseCurrencyRate.rate > 0f) {
                rateFormat.format(baseCurrencyRate.rate)
            } else ""

        currencies.add(
            BASE_CURRENCY_POSITION, Currency(
                baseCurrencyRate.code,
                baseCurrencyAmount,
                resourceManager.getCurrencyFlagResByCode(baseCurrencyRate.code)
            )
        )
    }

    private fun calculateConvertingRate(amount: Float, baseRate: Float): String {
        if (amount == 0f) return ""
        return rateFormat.format(amount * baseRate)
    }
}