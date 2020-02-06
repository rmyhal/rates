package com.rusmyhal.rates.di

import android.content.Context
import android.content.SharedPreferences
import com.rusmyhal.rates.core.NetworkClient
import com.rusmyhal.rates.core.Schedulers
import com.rusmyhal.rates.core.Storage
import com.rusmyhal.rates.core.impl.AppSchedulers
import com.rusmyhal.rates.core.impl.LocalPreferences
import com.rusmyhal.rates.core.impl.ResourcesManager
import com.rusmyhal.rates.core.impl.RetrofitClient
import com.rusmyhal.rates.feature.currencies.CurrenciesViewModel
import com.rusmyhal.rates.feature.currencies.data.CurrenciesApiService
import com.rusmyhal.rates.feature.currencies.data.CurrenciesRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DEFAULT_PREFERENCES_NAME = "user_preferences"

private val coreModule = module {
    single<NetworkClient> { RetrofitClient() }
    single { ResourcesManager(androidApplication()) }
    single<Schedulers> { AppSchedulers() }
    single<SharedPreferences> {
        androidApplication().getSharedPreferences(
            DEFAULT_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }
    single<Storage> { LocalPreferences(get()) }
}
private val currenciesModule = module {
    single {
        val networkClient: NetworkClient = get()
        CurrenciesRepository(networkClient.createService(CurrenciesApiService::class.java), get())
    }

    viewModel { CurrenciesViewModel(get(), get(), get()) }
}

val appModules = listOf(coreModule, currenciesModule)