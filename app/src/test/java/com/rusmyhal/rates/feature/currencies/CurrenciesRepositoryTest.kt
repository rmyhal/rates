package com.rusmyhal.rates.feature.currencies

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.rusmyhal.rates.TestUtil
import com.rusmyhal.rates.core.Storage
import com.rusmyhal.rates.feature.currencies.data.CurrenciesApiService
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.CurrencyRate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

@FlowPreview
@ExperimentalCoroutinesApi
class CurrenciesRepositoryTest {

    private val currenciesApiService: CurrenciesApiService = mock()
    private val localStorage: Storage = mock()

    private lateinit var repository: CurrenciesRepository

    @Before
    fun setup() {
        repository = CurrenciesRepository(currenciesApiService, localStorage)
    }

    @Test
    fun collectResponse_takeOne() = runBlockingTest {
        whenever(currenciesApiService.getCurrencies(anyOrNull())).thenReturn(TestUtil.CURRENCY_RESPONSE_1)

        repository.fetchCurrenciesRates().take(1).collect {
            assertEquals(listOf(TestUtil.CURRENCY_RATE_1), it)
        }
    }

    @Test
    fun collectResponse_collectFlow() = runBlockingTest {
        whenever(currenciesApiService.getCurrencies(anyOrNull())).thenReturn(
            TestUtil.CURRENCY_RESPONSE_1,
            TestUtil.CURRENCY_RESPONSE_2
        )

        val results = mutableListOf<List<CurrencyRate>>()
        val job = launch {
            repository.fetchCurrenciesRates().collect {
                when (results.size) {
                    0 -> {
                        results.add(it)
                    }
                    1 -> {
                        results.add(it)
                    }
                    else -> fail("Should have received only 2 values")
                }
            }
        }
        advanceTimeBy(1_000L) // this will cause delay to resume
        job.cancel()

        assertThat(results.size).isEqualTo(2)
        assertThat(results[0]).isEqualTo(listOf(TestUtil.CURRENCY_RATE_1))
        assertThat(results[1]).isEqualTo(listOf(TestUtil.CURRENCY_RATE_1, TestUtil.CURRENCY_RATE_2))
    }

    @Test
    fun saveCurrencyCode() {
        val currencyCode = "uah"
        repository.saveCurrencyCode(currencyCode)

        verify(localStorage).saveData("currency_code", currencyCode)
    }

    @Test
    fun getCurrencyCode_fromStorage() {
        val currencyCode = "uah"
        whenever(localStorage.getData(anyString())).thenReturn(currencyCode)

        val cachedCode = repository.getCachedCurrencyCode()
        verify(localStorage).getData(anyString())
        assertThat(cachedCode).isEqualTo(currencyCode)
    }

    @Test
    fun getCurrencyCode_cached() {
        val currencyCode = "uah"
        whenever(localStorage.getData(anyString())).thenReturn(currencyCode)

        repository.saveCurrencyCode(currencyCode)
        verify(localStorage).saveData("currency_code", currencyCode)
        repository.getCachedCurrencyCode()
        verifyNoMoreInteractions(localStorage)
    }

}