package com.rusmyhal.rates.core

import kotlinx.coroutines.CoroutineDispatcher

interface Schedulers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}