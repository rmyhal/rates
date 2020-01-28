package com.rusmyhal.rates.feature.currencies

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.rusmyhal.rates.TestUtil
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

@FlowPreview
@ExperimentalCoroutinesApi
class CurrenciesRepositoryTest {

    private val currenciesApiService: CurrenciesApiService = mock()

    private lateinit var repository: CurrenciesRepository

    @Before
    fun setup() {
        repository = CurrenciesRepository(currenciesApiService)
    }

    @Test
    fun collectResponse_takeOne() = runBlockingTest {
        whenever(currenciesApiService.getCurrencies(anyOrNull())).thenReturn(TestUtil.CURRENCY_RESPONSE_1)

        repository.fetchCurrenciesRates().take(1).collect {
            assertEquals(listOf(TestUtil.CURRENCY_RATE_1), it)
        }
    }

    @Test
    fun collectResponse_launch() = runBlockingTest {
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
}