package com.rusmyhal.rates

import com.rusmyhal.rates.core.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestSchedulers: Schedulers {
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
    override val main: CoroutineDispatcher = Dispatchers.Unconfined
    override val default: CoroutineDispatcher = Dispatchers.Unconfined
}