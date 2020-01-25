package com.rusmyhal.rates.core

interface NetworkClient {

    fun <T> createService(serviceClass: Class<T>): T
}