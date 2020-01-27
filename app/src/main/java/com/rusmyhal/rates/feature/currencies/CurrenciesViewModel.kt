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
    private var currenciesRates: List<CurrencyRate> = emptyList()
    private var baseRate = CurrencyRate(DEFAULT_CURRENCY_CODE, DEFAULT_CURRENCY_RATE)
    private var currentAmount: Float = baseRate.rate

    fun startUpdatingCurrencies() {
        currenciesJob = viewModelScope.launch(Dispatchers.Main) {
            currenciesRepository.fetchCurrenciesRates(baseRate.code)
                .conflate()
                .collect { rates ->
                    currenciesRates = rates
                    _currencies.postValue(mapCurrenciesRates(rates))
                }
        }
    }

    fun stopUpdatingCurrencies() {
        currenciesJob.cancel()
    }

    fun selectCurrency(currency: Currency) {
        if (currency.code == baseRate.code) return

        stopUpdatingCurrencies()
        baseRate = CurrencyRate(currency.code, currency.amount.toFloatOrNull() ?: 0f)
        currentAmount = baseRate.rate
        startUpdatingCurrencies()
    }

    fun onAmountChanged(newAmount: String) {
        currentAmount = if (newAmount.toDoubleOrNull() != null) {
            newAmount.toFloat()
        } else {
            0f
        }
        baseRate.rate = currentAmount
        _currencies.value = mapCurrenciesRates(currenciesRates)
    }

    private fun mapCurrenciesRates(currenciesRates: List<CurrencyRate>): List<Currency> {
        return currenciesRates.toMutableList()
            .map { currencyRate ->
                Currency(
                    currencyRate.code,
                    calculateConvertingRate(currentAmount, currencyRate.rate),
                    resourceManager.getCurrencyFlagResByCode(currencyRate.code)
                )
            }.toMutableList().apply {
                val baseCurrencyAmount =
                    if (baseRate.rate > 0f) {
                        decimalFormat.format(baseRate.rate)
                    } else ""

                add(
                    0, Currency(
                        baseRate.code,
                        baseCurrencyAmount,
                        resourceManager.getCurrencyFlagResByCode(baseRate.code)
                    )
                )
            }
    }

    private fun calculateConvertingRate(amount: Float, baseRate: Float): String {
        if (amount == 0f) return ""
        return decimalFormat.format(amount * baseRate)
    }
}