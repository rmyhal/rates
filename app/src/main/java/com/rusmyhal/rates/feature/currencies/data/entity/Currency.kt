package com.rusmyhal.rates.feature.currencies.data.entity

import androidx.annotation.DrawableRes
import java.util.Currency as JavaCurrency

data class Currency(
    val code: String,
    val amount: String,
    @DrawableRes val flagResId: Int
) {

    val displayName: String = JavaCurrency.getInstance(code).displayName
}