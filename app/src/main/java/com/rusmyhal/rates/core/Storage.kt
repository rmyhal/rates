package com.rusmyhal.rates.core

interface Storage {

    fun saveData(key: String, value: String)

    fun getData(key: String): String?
}