package com.rusmyhal.rates.core.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rusmyhal.rates.core.Storage

class LocalPreferences(private val sharedPreferences: SharedPreferences) : Storage {

    override fun saveData(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    override fun getData(key: String): String? = sharedPreferences.getString(key, null)
}