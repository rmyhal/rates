package com.rusmyhal.rates.di

import com.rusmyhal.rates.core.NetworkClient
import com.rusmyhal.rates.core.ResourcesManager
import com.rusmyhal.rates.core.Schedulers
import com.rusmyhal.rates.core.impl.AppSchedulers
import com.rusmyhal.rates.core.impl.RetrofitClient
import com.rusmyhal.rates.feature.currencies.CurrenciesViewModel
import com.rusmyhal.rates.feature.currencies.data.CurrenciesApiService
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


private val coreModule = module {
    single<NetworkClient> { RetrofitClient() }
    single { ResourcesManager(androidApplication()) }
    single<Schedulers> { AppSchedulers() }
}
private val currenciesModule = module {
    single {
        val networkClient: NetworkClient = get()
        CurrenciesRepository(networkClient.createService(CurrenciesApiService::class.java))
    }

    viewModel { CurrenciesViewModel(get(), get(), get()) }
}

val appModules = listOf(coreModule, currenciesModule)