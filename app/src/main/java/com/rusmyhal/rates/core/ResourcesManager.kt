package com.rusmyhal.rates.core

import android.content.Context
import androidx.annotation.StringRes
import com.rusmyhal.rates.util.test.OpenForTesting
import java.util.*

@OpenForTesting
class ResourcesManager(private val context: Context) {

    fun getCurrencyFlagResByCode(currencyCode: String): Int {
        return context.resources.getIdentifier(
            "ic_${currencyCode.toLowerCase(Locale.getDefault())}_flag",
            "drawable",
            context.packageName
        )
    }

    fun getString(@StringRes resId: Int): String = context.getString(resId)
}