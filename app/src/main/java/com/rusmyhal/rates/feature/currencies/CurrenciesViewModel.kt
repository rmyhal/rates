package com.rusmyhal.rates.feature.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.CurrenciesResponse

class CurrenciesViewModel(private val currenciesRepository: CurrenciesRepository) : ViewModel() {

    val currencies: LiveData<CurrenciesResponse> = liveData {
        emit(currenciesRepository.fetchCurrencies())
    }
}