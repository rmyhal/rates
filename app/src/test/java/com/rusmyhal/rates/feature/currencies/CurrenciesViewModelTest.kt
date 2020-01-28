package com.rusmyhal.rates.feature.currencies

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.rusmyhal.rates.core.ResourcesManager
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CurrenciesViewModelTest {

    private val repository: CurrenciesRepository = mock()
    private val resourcesManager: ResourcesManager = mock()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var viewModel: CurrenciesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = CurrenciesViewModel(repository, resourcesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testStartStop() = runBlockingTest {
        viewModel.startUpdatingCurrencies()
        verify(repository).fetchCurrenciesRates(anyOrNull())
    }
}