package com.rusmyhal.rates.feature.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rusmyhal.rates.R
import com.rusmyhal.rates.core.Schedulers
import com.rusmyhal.rates.core.impl.ResourcesManager
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat

@ExperimentalCoroutinesApi
class CurrenciesViewModel(
    private val currenciesRepository: CurrenciesRepository,
    private val resourceManager: ResourcesManager,
    private val schedulers: Schedulers
) : ViewModel() {

    companion object {
        private const val DEFAULT_CURRENCY_CODE = "EUR"
        private const val DEFAULT_CURRENCY_RATE = 1
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
    private var baseCurrencyRate = CurrencyRate(
        currenciesRepository.getLastSelectedCurrencyCode() ?: DEFAULT_CURRENCY_CODE,
        DEFAULT_CURRENCY_RATE.toBigDecimal()
    )

    private var currentAmount: BigDecimal = baseCurrencyRate.rate

    fun startUpdatingCurrencies() {
        currenciesJob = viewModelScope.launch(schedulers.default) {
            currenciesRepository.fetchCurrenciesRates(baseCurrencyRate.code)
                .retryWhen { cause, _ ->
                    // retry only when IOException
                    if (cause is IOException) {
                        _networkErrorMessage.postValue(resourceManager.getString(R.string.currencies_network_error))
                        delay(CURRENCIES_FETCHING_RETRY_DELAY_MILLIS)
                        true
                    } else {
                        false
                    }
                }
                .conflate()
                .collect { newRates ->
                    currenciesRates = newRates
                    _currencies.postValue(mapCurrenciesRates(newRates))

                    if (_networkErrorMessage.value != null) {
                        _networkErrorMessage.postValue(null)
                    }
                }
        }
    }

    fun stopUpdatingCurrencies() {
        currenciesJob.cancel()
    }

    fun selectCurrency(currency: Currency) {
        if (currency.code == baseCurrencyRate.code) return

        currenciesRepository.saveCurrencyCode(currency.code)

        stopUpdatingCurrencies()
        baseCurrencyRate =
            CurrencyRate(currency.code, currency.amount.toBigDecimalOrNull() ?: BigDecimal(0))
        currentAmount = baseCurrencyRate.rate
        startUpdatingCurrencies()
    }

    fun onAmountChanged(newAmount: String) {
        viewModelScope.launch(schedulers.default) {
            currentAmount = newAmount.toBigDecimalOrNull() ?: BigDecimal(0)
            baseCurrencyRate.rate = currentAmount

            _currencies.postValue(mapCurrenciesRates(currenciesRates))
        }
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
            if (baseCurrencyRate.rate > 0.toBigDecimal()) {
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

    private fun calculateConvertingRate(amount: BigDecimal, baseRate: BigDecimal): String {
        if (amount.compareTo(baseRate) == 0) return ""
        return rateFormat.format(amount.multiply(baseRate))
    }
}