package com.rusmyhal.rates.core

import android.content.Context
import java.util.*

class ResourcesManager(private val context: Context) {

    fun getCurrencyFlagResByCode(currencyCode: String): Int {
        return context.resources.getIdentifier(
            "ic_${currencyCode.toLowerCase(Locale.getDefault())}_flag",
            "drawable",
            context.packageName
        )
    }
}