package com.rusmyhal.rates.di

import com.rusmyhal.rates.currencies.CurrenciesRepository
import com.rusmyhal.rates.currencies.CurrenciesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


private val dataModule = module {
    single { CurrenciesRepository() }
}

private val viewModelsModule = module {
    viewModel { CurrenciesViewModel(get()) }
}

val appModules = listOf(dataModule, viewModelsModule)