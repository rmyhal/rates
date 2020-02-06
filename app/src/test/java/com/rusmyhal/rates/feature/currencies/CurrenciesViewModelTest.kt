package com.rusmyhal.rates.feature.currencies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.rusmyhal.rates.TestSchedulers
import com.rusmyhal.rates.TestUtil
import com.rusmyhal.rates.core.impl.ResourcesManager
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import com.rusmyhal.rates.feature.currencies.data.entity.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CurrenciesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository: CurrenciesRepository = mock()
    private val resourcesManager: ResourcesManager = mock()
    private val currenciesObserver: Observer<List<Currency>> = mock()
    private val networkErrorObserver: Observer<String?> = mock()
    private val schedulers = TestSchedulers()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var viewModel: CurrenciesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = CurrenciesViewModel(repository, resourcesManager, schedulers).apply {
            currencies.observeForever(currenciesObserver)
            networkErrorMessage.observeForever(networkErrorObserver)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun fetchCurrencies_success() = runBlockingTest {
        whenever(resourcesManager.getCurrencyFlagResByCode(any())).thenReturn(1)
        whenever(repository.fetchCurrenciesRates(anyOrNull())).thenReturn(
            listOf(
                listOf(
                    TestUtil.CURRENCY_RATE_1,
                    TestUtil.CURRENCY_RATE_2
                )
            ).asFlow()
        )

        viewModel.startUpdatingCurrencies()
        verify(repository).fetchCurrenciesRates(TestUtil.DEFAULT_CURRENCY.code)
        verify(currenciesObserver).onChanged(
            listOf(
                TestUtil.DEFAULT_CURRENCY,
                TestUtil.CURRENCY_1,
                TestUtil.CURRENCY_2
            )
        )
        // no errors
        verifyNoMoreInteractions(networkErrorObserver)
    }

    @Test
    fun selectDefaultCurrency_doNothing() = runBlockingTest {
        verify(repository).getLastSelectedCurrencyCode()
        viewModel.selectCurrency(TestUtil.DEFAULT_CURRENCY)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun selectNewCurrency() = runBlockingTest {
        val currenciesList = listOf(TestUtil.CURRENCY_RATE_1, TestUtil.CURRENCY_RATE_2)
        whenever(repository.fetchCurrenciesRates(anyOrNull())).thenReturn(listOf(currenciesList).asFlow())

        viewModel.startUpdatingCurrencies()
        verify(repository).fetchCurrenciesRates(TestUtil.DEFAULT_CURRENCY.code)

        viewModel.selectCurrency(TestUtil.CURRENCY_1)
        verify(repository).saveCurrencyCode(TestUtil.CURRENCY_1.code)
        verify(repository).fetchCurrenciesRates(TestUtil.CURRENCY_1.code)
    }

    @Test
    fun updateAmount_aboveZero() = runBlockingTest {
        val currenciesList = listOf(TestUtil.CURRENCY_RATE_1, TestUtil.CURRENCY_RATE_2)
        whenever(repository.fetchCurrenciesRates(anyOrNull())).thenReturn(listOf(currenciesList).asFlow())
        whenever(resourcesManager.getCurrencyFlagResByCode(anyString())).thenReturn(1)

        val newAmount = "10.00"
        viewModel.startUpdatingCurrencies()
        verify(currenciesObserver).onChanged(any())

        viewModel.onAmountChanged(newAmount)

        val convertedCurrenciesList = listOf(TestUtil.DEFAULT_CURRENCY_X10, TestUtil.CURRENCY_1_X10, TestUtil.CURRENCY_2_X10)
        verify(currenciesObserver).onChanged(convertedCurrenciesList)
    }

    @Test
    fun updateAmount_zero() = runBlockingTest {
        val currenciesList = listOf(TestUtil.CURRENCY_RATE_1, TestUtil.CURRENCY_RATE_2)
        whenever(repository.fetchCurrenciesRates(anyOrNull())).thenReturn(listOf(currenciesList).asFlow())
        whenever(resourcesManager.getCurrencyFlagResByCode(anyString())).thenReturn(1)

        val newAmount = "0"
        viewModel.startUpdatingCurrencies()
        verify(currenciesObserver).onChanged(any())

        viewModel.onAmountChanged(newAmount)

        val convertedCurrenciesList = listOf(TestUtil.DEFAULT_CURRENCY_X0, TestUtil.CURRENCY_1_X0, TestUtil.CURRENCY_2_X0)
        verify(currenciesObserver).onChanged(convertedCurrenciesList)
    }
}