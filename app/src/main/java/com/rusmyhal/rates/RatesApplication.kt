package com.rusmyhal.rates

import android.app.Application
import com.rusmyhal.rates.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RatesApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(applicationContext)
            modules(appModules)
        }
    }
}